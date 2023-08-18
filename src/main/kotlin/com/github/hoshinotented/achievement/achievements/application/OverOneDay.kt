package com.github.hoshinotented.achievement.achievements.application

import com.github.hoshinotented.achievement.AchievementMain
import com.github.hoshinotented.achievement.core.ProjectAchievement
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.util.messages.MessageBusConnection
import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.days

class OverOneDay : AbstractAchievement(
  "project.overOneDay",
  "Over 24 hours",
  "Coding over 24 hours, I am worried about your health...",
  false
), ProjectAchievement {
  val coroutineScope = CoroutineScope(EmptyCoroutineContext)
  val coroutineContext = newSingleThreadContext("OverOneDay")
  val messageBus : MessageBusConnection = ApplicationManager.getApplication().messageBus.connect()
  var job : Job? = null
  
  init {
    messageBus.subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
      override fun projectClosed(project : Project) {
        val reset = ProjectManager.getInstance().openProjects.isEmpty()
        if (reset) {
          invokeLater {
            job?.cancel()
            job = null
          }
        }
      }
    })
  }
  
  override suspend fun init(project : Project) {
    invokeLater {
      if (isCompleted) return@invokeLater
      val thisJob = job
      if (thisJob == null || thisJob.isCompleted) {
        if (thisJob?.isCompleted == true) AchievementMain.LOG.warn("NotNull Completed Job")
        job = coroutineScope.launch {
          delay(1.days)
          onComplete()
        }
      }
    }
  }
  
  fun onComplete() {
    invokeLater {
      messageBus.disconnect()
      AchievementMain.onComplete(this@OverOneDay)
    }
  }
  
  fun invokeLater(block : suspend CoroutineScope.() -> Unit) {
    coroutineScope.launch(context = coroutineContext, block = block)
  }
}