package com.github.hoshinotented.achievement

import com.github.hoshinotented.achievement.achievements.application.FirstStartUp
import com.github.hoshinotented.achievement.achievements.application.Typer
import com.github.hoshinotented.achievement.achievements.application.YuanShen
import com.github.hoshinotented.achievement.achievements.project.OverOneDay
import com.github.hoshinotented.achievement.core.Achievement
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.github.hoshinotented.achievement.core.ProjectAchievement
import com.github.hoshinotented.achievement.notification.AchievementNotification
import com.github.hoshinotented.achievement.services.AchievementData
import com.intellij.ide.DataManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import kala.collection.Seq
import kala.collection.SeqView
import org.quartz.Scheduler
import org.quartz.impl.StdSchedulerFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class AchievementPlugin : Disposable {
  companion object {
    val LOG = Logger.getInstance(AchievementPlugin::class.java)
    
    val SCHEDULER : Scheduler = StdSchedulerFactory.getDefaultScheduler()
    
    val INSTANCE : AchievementPlugin
      get() = ApplicationManager.getApplication()
        .getService(AchievementPlugin::class.java)
    
    val achievements : Seq<Achievement> = Seq.of(
      FirstStartUp(),
      OverOneDay(),
      Typer(),
      YuanShen()
    )
    
    init {
      achievements.forEach {
        val data = AchievementData.INSTANCE.myState.data[it.id] ?: return@forEach
        it.deserialize(data)
        
        // disposer
        Disposer.register(INSTANCE, it)
      }
    }
    
    fun projectAchieve() : SeqView<ProjectAchievement> = achievements.view()
      .filterIsInstance(ProjectAchievement::class.java)
      .filterNot { it.isCompleted }
    
    fun applicationAchieve() : SeqView<ApplicationAchievement> = achievements.view()
      .filterIsInstance(ApplicationAchievement::class.java)
      .filterNot { it.isCompleted }
    
    // TODO: fancy ui!!
    fun complete(achi : Achievement) {
      if (!achi.isCompleted) {
        achi.isCompleted = true
        Disposer.dispose(achi)
        
        val notification = AchievementNotification.achievement
          .createNotification(achi.name, achi.description, NotificationType.INFORMATION)
        try {
          DataManager.getInstance().dataContextFromFocusAsync
            .then {
              val proj = CommonDataKeys.PROJECT.getData(it)
              notification.notify(proj)
            }.blockingGet(1, TimeUnit.SECONDS)
        } catch (e : TimeoutException) {
          LOG.error(e)
        }
        
        LOG.info("Achievement ${achi.id} completed.")
      } else {
        LOG.warn("Achievement ${achi.id} was completed twice, something is wrong.")
      }
    }
  }
  
  override fun dispose() {
  }
}