package com.github.hoshinotented.achievement.achievements

import com.github.hoshinotented.achievement.AchievementPlugin
import com.github.hoshinotented.achievement.core.Achievement

fun Achievement.complete() {
  AchievementPlugin.complete(this)
}