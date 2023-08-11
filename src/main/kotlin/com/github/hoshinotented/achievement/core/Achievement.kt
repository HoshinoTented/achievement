package com.github.hoshinotented.achievement.core

import com.github.hoshinotented.achievement.AchievementMain
import com.github.hoshinotented.achievement.Bundle
import com.github.hoshinotented.achievement.services.AchievementData
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls

/**
 * # Achievement
 *
 * ## Lifecycle
 *
 * 1. Achievements are initialized when open idea/project (according their [Achievement.Type]s)
 * 2. Achievements should setup listener during initializing
 * 3. Once an achievement is completed, achievement should stop their listener, sets [isCompleted] true, and runs [AchievementMain.onComplete]
 */
interface Achievement {
  
  /**
   * Type of [Achievement]
   * * Application: Achievement only initializes once (after first project opens)
   * * Project: Achievement initializes when a project opens
   */
  enum class Type {
    Application,
    Project
  }
  
  val id : String
  
  /**
   * Name of achievement
   */
  @get:Nls
  val name : String
  
  @get:Nls
  val description : String
  
  @get:Nls
  val progress : String
    get() {
      return if (isCompleted)
        Bundle.message("achievement.complete.name")
      else
        Bundle.message("achievement.incomplete.name")
    }
  
  /**
   * If this achievement is hidden: invisible until user completes it.
   */
  val isHidden : Boolean
  
  val isCompleted : Boolean
    get() {
      return AchievementData.INSTANCE.isComplete(this)
    }
  
  val type : Type
}

interface ProjectAchievement : Achievement {
  override val type : Achievement.Type get() = Achievement.Type.Project
  
  suspend fun init(project : Project)
}

interface ApplicationAchievement : Achievement {
  override val type : Achievement.Type get() = Achievement.Type.Application
  
  suspend fun init()
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AchievementMarker