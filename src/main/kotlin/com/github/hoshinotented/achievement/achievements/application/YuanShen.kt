package com.github.hoshinotented.achievement.achievements.application

import com.github.hoshinotented.achievement.AchievementPlugin
import com.github.hoshinotented.achievement.achievements.AbstractAchievement
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.intellij.openapi.util.Disposer
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.minutes

class YuanShen : AbstractAchievement(
  "application.yuanShen",
  false
), ApplicationAchievement {
  companion object {
    val SCOPE : CoroutineScope = CoroutineScope(EmptyCoroutineContext)
    val COMMAND : Array<String> = arrayOf("PowerShell.exe", "-Command", "Get-Process YuanShen")
  }
  
  @Volatile
  var job : Job? = null
  
  override suspend fun init() {
    job = SCOPE.launch {
      while (isActive) {
        delay(1.minutes)
        
        val process = Runtime.getRuntime().exec(COMMAND)
        val exited = process.waitFor(5, TimeUnit.SECONDS)
        
        if (!exited) {
          AchievementPlugin.LOG.error("Command execution timeout")
          Disposer.dispose(this@YuanShen)
          break
        } else {
          if (process.exitValue() == 0) {
            // YuanShen is running
            AchievementPlugin.complete(this@YuanShen)
            break
          }
        }
      }
    }
  }
  
  override fun dispose() {
    runBlocking {
      job?.cancelAndJoin()
    }
  }
}