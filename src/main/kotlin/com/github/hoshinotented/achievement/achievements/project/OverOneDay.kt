package com.github.hoshinotented.achievement.achievements.project

import com.github.hoshinotented.achievement.AchievementPlugin
import com.github.hoshinotented.achievement.achievements.AbstractAchievement
import com.github.hoshinotented.achievement.core.ProjectAchievement
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.util.messages.MessageBusConnection
import kala.value.AtomicVar
import kotlinx.coroutines.*
import kotlinx.datetime.Instant
import java.util.*
import kotlin.time.Duration.Companion.days

class OverOneDay : AbstractAchievement(
  "project.overOneDay",
  "Over 24 hours",
  "Coding over 24 hours, I am worry about your health...",
  false
), ProjectAchievement {
  private val messageBus: MessageBusConnection = ApplicationManager.getApplication().messageBus.connect()
  private var job: Job? = null
  
  init {
    messageBus.subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
      override fun projectClosed(project : Project) {
        val reset = ProjectManager.getInstance().openProjects.isEmpty()
        if (reset) {
          onReset()
        }
      }
    })
  }
  
  override suspend fun init(project : Project) {
    synchronized(this) {
      val mJob = job
      if (!isCompleted && (mJob == null || !mJob.isActive)) {
        job = AchievementPlugin.SCOPE.launch {
          delay(1.days)
          onComplete()
        }
      }
    }
  }
  
  private fun onReset() {
    synchronized(this) {
      job?.cancel()
    }
  }
  
  private fun onComplete() {
    synchronized(this) {
      AchievementPlugin.complete(this@OverOneDay)
    }
  }
  
  override fun dispose() {
    messageBus.disconnect()
    job?.cancel()
  }
}