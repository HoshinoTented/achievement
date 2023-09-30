package com.github.hoshinotented.achievement.achievements.project

import com.github.hoshinotented.achievement.AchievementPlugin
import com.github.hoshinotented.achievement.achievements.AbstractAchievement
import com.github.hoshinotented.achievement.core.ProjectAchievement
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.util.messages.MessageBusConnection
import kotlinx.coroutines.*
import kotlinx.coroutines.Job
import org.quartz.*
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.days

class OverOneDay : AbstractAchievement(
  "project.overOneDay",
  "Over 24 hours",
  "Coding over 24 hours, I am worry about your health...",
  false
), ProjectAchievement, org.quartz.Job {
  private val messageBus : MessageBusConnection = ApplicationManager.getApplication().messageBus.connect()
  private val jobKey : JobKey = JobKey.jobKey("overOneDay", "achievements")
  private val myJob : JobDetail = JobBuilder.newJob(OverOneDay::class.java)
    .withIdentity(jobKey)
    .build()
  
  private val trigger : Trigger = TriggerBuilder.newTrigger()
    .startAt(Date(System.currentTimeMillis() + 1.days.inWholeMilliseconds))
    .build()
  
  init {
    messageBus.subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
      override fun projectClosed(project : Project) {
        val reset = ProjectManager.getInstance().openProjects.isEmpty()
        if (reset) {
          synchronized(this@OverOneDay) {
            AchievementPlugin.SCHEDULER.deleteJob(jobKey)
          }
        }
      }
    })
  }
  
  override fun execute(context : JobExecutionContext?) {
    onComplete()
  }
  
  override suspend fun init(project : Project) {
    synchronized(this) {
      if (!isCompleted && !AchievementPlugin.SCHEDULER.checkExists(jobKey)) {
        AchievementPlugin.SCHEDULER.scheduleJob(myJob, trigger)
      }
    }
  }
  
  private fun onComplete() {
    synchronized(this) {
      AchievementPlugin.complete(this@OverOneDay)
    }
  }
  
  override fun dispose() {
    messageBus.disconnect()
    AchievementPlugin.SCHEDULER.deleteJob(jobKey)
  }
}