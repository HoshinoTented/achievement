package com.github.hoshinotented.achievement.achievements.application

import com.github.hoshinotented.achievement.AchievementMain
import com.github.hoshinotented.achievement.core.AchievementMarker
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.intellij.openapi.application.ApplicationManager

@AchievementMarker
class FirstStartUp : ApplicationAchievement {
  override val id : String get() = "application.firstStartUp"
  override val name : String get() = "First Start Up!"
  override val description : String get() = "Welcome to IntelliJ IDEA"
  override val isHidden : Boolean get() = false
  
  override suspend fun init() {
    ApplicationManager.getApplication().invokeLater {
      AchievementMain.onComplete(this)
    }
  }
}