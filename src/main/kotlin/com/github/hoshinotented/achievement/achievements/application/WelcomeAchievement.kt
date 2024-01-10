package com.github.hoshinotented.achievement.achievements.application

import com.github.hoshinotented.achievement.AchievementPlugin
import com.github.hoshinotented.achievement.achievements.AbstractAchievement
import com.github.hoshinotented.achievement.core.AchievementMarker
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.intellij.openapi.application.ApplicationManager

@AchievementMarker
class WelcomeAchievement : AbstractAchievement("application.welcome", false), ApplicationAchievement {
  override suspend fun init() {
    ApplicationManager.getApplication().invokeLater {
      AchievementPlugin.complete(this)
    }
  }
  
  override fun dispose() {
  }
}