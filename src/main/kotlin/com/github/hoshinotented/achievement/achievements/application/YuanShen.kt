package com.github.hoshinotented.achievement.achievements.application

import com.github.hoshinotented.achievement.AchievementPlugin
import com.github.hoshinotented.achievement.achievements.AbstractAchievement
import com.github.hoshinotented.achievement.achievements.complete
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.github.hoshinotented.achievement.util.runCommand
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class YuanShen : AbstractAchievement(
  "application.yuanShen",
  false
), ApplicationAchievement {
  companion object {
    val COMMAND : Array<String> = arrayOf("PowerShell.exe", "-Command", "Get-Process YuanShen")
  }
  
  private var job: Job? = null
  override suspend fun init() {
    job = AchievementPlugin.SCOPE.launch {
      delay(1.minutes)
      val exitCode = runCommand(COMMAND)
      if (exitCode == 0) {
        complete()
      }
    }
  }
  
  override fun dispose() {
    job?.cancel()
  }
}