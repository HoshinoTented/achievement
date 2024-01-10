package com.github.hoshinotented.achievement

import com.github.hoshinotented.achievement.achievements.application.AgdaKeybindAchievement
import com.github.hoshinotented.achievement.achievements.application.WelcomeAchievement
import com.github.hoshinotented.achievement.achievements.application.TypingAchievement
import com.github.hoshinotented.achievement.achievements.application.YuanShenAchievement
import com.github.hoshinotented.achievement.achievements.project.BadExecutionAchievement
import com.github.hoshinotented.achievement.achievements.project.OpenAyaWithoutAyaPluginAchievement
import com.github.hoshinotented.achievement.achievements.project.CodeOneDayAchievement
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
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.coroutines.EmptyCoroutineContext

class AchievementPlugin : Disposable {
  companion object {
    val LOG = Logger.getInstance(AchievementPlugin::class.java)
    
    val SCOPE: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
    
    val INSTANCE : AchievementPlugin
      get() = ApplicationManager.getApplication()
        .getService(AchievementPlugin::class.java)
    
    val achievements : Seq<Achievement> = Seq.of(
      WelcomeAchievement(),
      CodeOneDayAchievement(),
      TypingAchievement,
      YuanShenAchievement(),
      BadExecutionAchievement(),
      OpenAyaWithoutAyaPluginAchievement(),
      AgdaKeybindAchievement
    )
    
    init {
      achievements.forEach(::initializeAchieve)
    }
    
    fun initializeAchieve(achi: Achievement) {
      val data = AchievementData.INSTANCE.myState.data[achi.id] ?: return
      achi.deserialize(data)
      
      Disposer.register(INSTANCE, achi)
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
        
        LOG.info("Achievement ${achi.id} is completed.")
      } else {
        LOG.warn("Achievement ${achi.id} is completed twice, something is wrong.")
      }
    }
  }
  
  override fun dispose() {
  }
}