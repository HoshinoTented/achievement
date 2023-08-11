package com.github.hoshinotented.achievement.achievements.application

import com.github.hoshinotented.achievement.core.Achievement

abstract class AbstractAchievement(
  override val id : String,
  override val name : String,
  override val description : String,
  override val isHidden : Boolean
) : Achievement {
  override var isCompleted : Boolean = false
}