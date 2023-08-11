package com.github.hoshinotented.achievement.notification

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager

object AchievementNotification {
  val achievement : NotificationGroup =
    NotificationGroupManager.getInstance()
      .getNotificationGroup("com.github.hoshinotented.achievement.notification.AchievementNotification")
}