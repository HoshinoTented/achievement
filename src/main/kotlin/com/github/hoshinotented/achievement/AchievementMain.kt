package com.github.hoshinotented.achievement

import com.github.hoshinotented.achievement.achievements.application.FirstStartUp
import com.github.hoshinotented.achievement.core.Achievement
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.github.hoshinotented.achievement.core.ProjectAchievement
import com.github.hoshinotented.achievement.notification.AchievementNotification
import com.github.hoshinotented.achievement.services.AchievementData
import com.intellij.ide.DataManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger
import kala.collection.Seq
import kala.collection.SeqView
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object AchievementMain {
  val LOG = Logger.getInstance(AchievementMain::class.java)
  
  val achievements : Seq<Achievement> = Seq.of(
    FirstStartUp()
  )
  
  init {
    achievements.forEach {
      val data = AchievementData.INSTANCE.myState.data[it.id] ?: return@forEach
      it.deserialize(data)
    }
  }
  
  fun projectAchieve() : SeqView<ProjectAchievement> = achievements.view()
    .filterIsInstance(ProjectAchievement::class.java)
    .filterNot { it.isCompleted }
  
  fun applicationAchieve() : SeqView<ApplicationAchievement> = achievements.view()
    .filterIsInstance(ApplicationAchievement::class.java)
    .filterNot { it.isCompleted }
  
  // TODO: fancy ui!!
  fun onComplete(ache : Achievement) {
    if (!ache.isCompleted) {
      ache.isCompleted = true
      val notification = AchievementNotification.achievement
        .createNotification(ache.name, ache.description, NotificationType.INFORMATION)
      try {
        DataManager.getInstance().dataContextFromFocusAsync
          .then {
            val proj = CommonDataKeys.PROJECT.getData(it)
            notification.notify(proj)
          }.blockingGet(1, TimeUnit.SECONDS)
      } catch (e : TimeoutException) {
        LOG.error(e)
      }
    } else {
      LOG.warn("Achievement ${ache.id} was completed twice, something is wrong.")
    }
  }
}