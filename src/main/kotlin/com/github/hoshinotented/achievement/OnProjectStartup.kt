package com.github.hoshinotented.achievement

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

class OnProjectStartup : StartupActivity {
  val applicationStartup : AtomicBoolean = AtomicBoolean(false)
  
  override fun runActivity(project : Project) {
    if (!applicationStartup.getAndSet(true)) {
      AchievementMain.applicationAchieve().forEach {
        AchievementMain.LOG.info("Initializing application achievement ${it.name} (${it.id})")
        runBlocking {
          it.init()
        }
        AchievementMain.LOG.info("Initialized application achievement ${it.name} (${it.id})")
      }
    }
    
    AchievementMain.projectAchieve().forEach {
      AchievementMain.LOG.info("Initializing project achievement ${it.name} (${it.id})")
      runBlocking {   // Any meaning?
        it.init(project)
      }
      AchievementMain.LOG.info("Initialized project achievement ${it.name} (${it.id})")
    }
  }
}