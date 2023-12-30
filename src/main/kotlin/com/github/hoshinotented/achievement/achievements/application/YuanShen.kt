package com.github.hoshinotented.achievement.achievements.application

import com.github.hoshinotented.achievement.AchievementPlugin
import com.github.hoshinotented.achievement.achievements.AbstractAchievement
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.github.hoshinotented.achievement.util.runCommand
import com.intellij.openapi.util.Disposer
import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobExecutionContext
import org.quartz.JobKey
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

class YuanShen : AbstractAchievement(
  "application.yuanShen",
  false
), ApplicationAchievement, Job {
  companion object {
    val COMMAND : Array<String> = arrayOf("PowerShell.exe", "-Command", "Get-Process YuanShen")
    private val jobKey = JobKey.jobKey("yuanShen", "achievements")
    private val jobDetail = JobBuilder.newJob(YuanShen::class.java)
      .withIdentity(jobKey)
      .build()
    private val trigger = TriggerBuilder.newTrigger()
      .startNow()
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule()
          .withIntervalInMinutes(1)
      )
      .build()
  }
  
  // TODO: what if this function takes 1 minutes?
  override fun execute(context: JobExecutionContext?) {
    val exitCode = runCommand(COMMAND)
    if (exitCode == 0) {
      AchievementPlugin.complete(this)
    }
  }
  
  override suspend fun init() {
    AchievementPlugin.SCHEDULER.scheduleJob(jobDetail, trigger)
  }
  
  override fun dispose() {
    AchievementPlugin.SCHEDULER.deleteJob(jobKey)
  }
}