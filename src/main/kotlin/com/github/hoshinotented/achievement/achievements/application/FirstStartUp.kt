package com.github.hoshinotented.achievement.achievements.application

import com.github.hoshinotented.achievement.AchievementPlugin
import com.github.hoshinotented.achievement.core.AchievementMarker
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.intellij.openapi.application.ApplicationManager

@AchievementMarker
class FirstStartUp : ApplicationAchievement {
  override val id : String = "application.firstStartUp"
  
  override val name : String = "First Start Up!"
  
  override val description : String = "Welcome to IntelliJ IDEA"
  
  override val isHidden : Boolean = false
  
  override var isCompleted : Boolean = false
  
  override suspend fun init() {
    ApplicationManager.getApplication().invokeLater {
      AchievementPlugin.onComplete(this)
    }
  }
  
  override fun dispose() {
  }
}