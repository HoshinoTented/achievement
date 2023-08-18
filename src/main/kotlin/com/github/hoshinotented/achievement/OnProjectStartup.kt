package com.github.hoshinotented.achievement

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

class OnProjectStartup : StartupActivity {
  val applicationStartup : AtomicBoolean = AtomicBoolean(false)
  
  override fun runActivity(project : Project) {
    if (!applicationStartup.getAndSet(true)) {
      AchievementPlugin.applicationAchieve().forEach {
        AchievementPlugin.LOG.info("Initializing application achievement ${it.name} (${it.id})")
        runBlocking {
          it.init()
        }
        AchievementPlugin.LOG.info("Initialized application achievement ${it.name} (${it.id})")
      }
    }
    
    AchievementPlugin.projectAchieve().forEach {
      AchievementPlugin.LOG.info("Initializing project achievement ${it.name} (${it.id})")
      runBlocking {   // Any meaning?
        it.init(project)
      }
      AchievementPlugin.LOG.info("Initialized project achievement ${it.name} (${it.id})")
    }
  }
}