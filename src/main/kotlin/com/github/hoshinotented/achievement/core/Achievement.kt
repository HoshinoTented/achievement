package com.github.hoshinotented.achievement.core

import com.github.hoshinotented.achievement.AchievementPlugin
import com.github.hoshinotented.achievement.Bundle
import com.github.hoshinotented.achievement.achievements.complete
import com.github.hoshinotented.achievement.services.AchievementData
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls

/**
 * # Achievement
 *
 * ## Lifecycle
 *
 * 1. Achievements are initialized when open idea/project (according to their [Achievement.Type]s)
 * 2. Achievements should setup listener during initializing
 * 3. Once an achievement is completed, calls [AchievementPlugin.complete], and dispose everything in [Disposable.dispose]
 */
interface Achievement : Disposable {
  
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
  
  var isCompleted : Boolean
  
  val type : Type
  
  fun serialize(map : MutableMap<String, String>) {
    map[AchievementData.KEY_COMPLETED] = isCompleted.toString()
  }
  
  fun deserialize(map : Map<String, String>) {
    this.isCompleted = map[AchievementData.KEY_COMPLETED]?.toBooleanStrictOrNull() ?: false
  }
}

interface ProgressAchievement : Achievement {
  var current : Int
  val target : Int
  
  override val progress : String get() = "$current of $target"
  
  override fun serialize(map : MutableMap<String, String>) {
    super.serialize(map)
    map[AchievementData.KEY_PROGRESS] = current.toString()
  }
  
  override fun deserialize(map : Map<String, String>) {
    super.deserialize(map)
    this.current = map[AchievementData.KEY_PROGRESS]?.toIntOrNull() ?: 0
  }
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