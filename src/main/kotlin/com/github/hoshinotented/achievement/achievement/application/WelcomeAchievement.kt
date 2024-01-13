package com.github.hoshinotented.achievement.achievement.application

import com.github.hoshinotented.achievement.achievement.AbstractAchievement
import com.github.hoshinotented.achievement.core.AchievementMarker
import com.github.hoshinotented.achievement.core.ApplicationAchievement

@AchievementMarker
class WelcomeAchievement : AbstractAchievement("application.welcome", false), ApplicationAchievement {
  override fun init() {
    complete()
  }
  
  override fun dispose() {
  }
}