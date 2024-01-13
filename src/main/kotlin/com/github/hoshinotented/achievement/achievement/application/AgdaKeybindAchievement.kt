package com.github.hoshinotented.achievement.achievement.application

import com.github.hoshinotented.achievement.AchievementPlugin
import com.github.hoshinotented.achievement.achievement.AbstractAchievement
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.intellij.ide.IdeEventQueue
import kotlinx.coroutines.launch
import java.awt.event.KeyEvent

object AgdaKeybindAchievement : AbstractAchievement("application.agdaKeybind", true), ApplicationAchievement {
  private var ready: Boolean = false
  
  private fun isCc(e: KeyEvent): Boolean {
    return e.isControlDown && e.keyCode == KeyEvent.VK_C
  }
  
  private fun isCl(e: KeyEvent): Boolean {
    return e.isControlDown && e.keyCode == KeyEvent.VK_L
  }
  
  private fun isBlank(e: KeyEvent): Boolean {
    return e.keyCode == KeyEvent.VK_CONTROL
  }
  
  override fun init() {
    IdeEventQueue.getInstance().addPostprocessor({
      if (it is KeyEvent && it.id == KeyEvent.KEY_PRESSED) {
        if (!isBlank(it)) {   // ignore single CONTROL pressing
          if (!ready) {
            if (isCc(it)) {
              ready = true
            }
          } else {
            if (isCl(it)) {
              AchievementPlugin.SCOPE.launch {
                complete()    // probably takes long time
              }
            } else {
              ready = false
            }
          }
        }
      }
      
      return@addPostprocessor false
    }, this)
  }
  
  override fun dispose() {
  }
}