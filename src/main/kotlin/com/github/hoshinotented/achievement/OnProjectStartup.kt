package com.github.hoshinotented.achievement

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class OnProjectStartup : StartupActivity {
  override fun runActivity(project : Project) {
    val manager = AchievementManager.INSTANCE
    
    manager.initializeApplicationAchievement()
    
    if (!project.isDisposed) {
      manager.initializeProjectAchievement(project)
    }
  }
}