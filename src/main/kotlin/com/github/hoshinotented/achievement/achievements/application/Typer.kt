package com.github.hoshinotented.achievement.achievements.application

import com.github.hoshinotented.achievement.AchievementPlugin
import com.github.hoshinotented.achievement.achievements.AbstractAchievement
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.github.hoshinotented.achievement.core.ProgressAchievement
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.BulkAwareDocumentListener
import com.intellij.openapi.editor.event.DocumentEvent
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class Typer : AbstractAchievement(
  "application.typer",
  "Typer",
  "Typing over 10000 characters",
  false
), ApplicationAchievement, ProgressAchievement {
  companion object {
    const val TARGET : Int = 10000
  }
  
  override val target : Int = TARGET
  override var current : Int
    get() = count.get()
    set(value) {
      count.set(value)
    }
  
  val count : AtomicInteger = AtomicInteger(0)
  
  class Listener(val typer : Typer) : BulkAwareDocumentListener {
    private val atom : AtomicBoolean = AtomicBoolean(false)
    
    override fun documentChangedNonBulk(event : DocumentEvent) {
      println(event.newFragment)
      
      val length = event.newLength
      val result = typer.count.addAndGet(length)
      
      if (result >= TARGET) {
        if (!atom.getAndSet(true)) {
          AchievementPlugin.complete(typer)
        }
      }
    }
  }
  
  override suspend fun init() {
    EditorFactory.getInstance().eventMulticaster.addDocumentListener(Listener(this), this)
  }
  
  override fun dispose() {
  }
}