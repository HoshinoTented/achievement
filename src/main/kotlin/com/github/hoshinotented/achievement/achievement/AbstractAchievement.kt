package com.github.hoshinotented.achievement.achievement

import com.github.hoshinotented.achievement.AchievementManager
import com.github.hoshinotented.achievement.Bundle
import com.github.hoshinotented.achievement.core.Achievement

abstract class AbstractAchievement(
  override val id : String,
  override val name : String,
  override val description : String,
  override val isHidden : Boolean
) : Achievement {
  override var isCompleted : Boolean = false
  
  private val jobComplete: Lazy<Unit> = lazy {
    AchievementManager.INSTANCE.completeAchievement(this)
  }
  
  constructor(id : String, isHidden : Boolean) : this(
    id,
    Bundle.message("$id.name"),
    Bundle.message("$id.description"),
    isHidden
  )
  
  fun complete() {
    return jobComplete.value
  }
}