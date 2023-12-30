package com.github.hoshinotented.achievement.notification

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.util.containers.concat
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

object AchievementNotification {
  val achievement : NotificationGroup =
    NotificationGroupManager.getInstance()
      .getNotificationGroup("com.github.hoshinotented.achievement.notification.AchievementNotification")
  
  fun message(title: String, message: String, type: NotificationType) {
    if (!SystemTray.isSupported()) return
    
    val trayType = when (type) {
      NotificationType.IDE_UPDATE -> throw UnsupportedOperationException()
      NotificationType.INFORMATION -> TrayIcon.MessageType.INFO
      NotificationType.WARNING -> TrayIcon.MessageType.WARNING
      NotificationType.ERROR -> TrayIcon.MessageType.ERROR
    }
    
    val image = this.javaClass.getResource("icon.png") ?: throw IllegalStateException()
    val trayIcon = TrayIcon(Toolkit.getDefaultToolkit().createImage(image))
    trayIcon.isImageAutoSize = true
    
    SystemTray.getSystemTray().run {
      add(trayIcon)
      trayIcon.displayMessage(title, message, trayType)
      remove(trayIcon)
    }
  }
}