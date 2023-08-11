package com.github.hoshinotented.achievement.achievements.application

import com.github.hoshinotented.achievement.AchievementMain
import com.github.hoshinotented.achievement.core.ProjectAchievement
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.util.messages.MessageBusConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.days

class OverOneDay : AbstractAchievement(
  "project.overOneDay",
  "Over 24 hours",
  "Coding over 24 hours, I am worried about your health...",
  false
), ProjectAchievement {
  
  val messageBus : MessageBusConnection = ApplicationManager.getApplication().messageBus.connect()
  val mutex = Mutex(false)
  var job : Job? = null
  
  init {
    messageBus.subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
      override fun projectClosed(project : Project) {
        val reset = ProjectManager.getInstance().openProjects.isEmpty()
        if (reset) {
          CoroutineScope(EmptyCoroutineContext).launch {
            mutex.withLock(this@OverOneDay) {
              job?.cancel()
            }
          }
        }
      }
    })
  }
  
  override suspend fun init(project : Project) {
    mutex.withLock(this) {
      if (isCompleted) return@withLock
      if (job == null) {
        job = CoroutineScope(EmptyCoroutineContext).launch {
          delay(1.days)
          onComplete()
        }
      }
    }
  }
  
  suspend fun onComplete() {
    mutex.withLock(this) {
      messageBus.disconnect()
      AchievementMain.onComplete(this)
    }
  }
}