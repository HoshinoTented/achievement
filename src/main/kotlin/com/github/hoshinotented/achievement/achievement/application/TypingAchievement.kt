package com.github.hoshinotented.achievement.achievement.application

import com.github.hoshinotented.achievement.achievement.AbstractAchievement
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.github.hoshinotented.achievement.core.ProgressAchievement
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import java.util.concurrent.atomic.AtomicBoolean

class TypingAchievementHandler : TypedHandlerDelegate() {
  override fun newTypingStarted(c: Char, editor: Editor, context: DataContext) {
    with(TypingAchievement) {
      if (initialized.get()) {
        count += 1
        println(count)
        if (count >= target) {
          complete()
        }
      }
    }
  }
}

object TypingAchievement : AbstractAchievement(
  "application.typer",
  "Typer",
  "Typing over 10000 characters",
  false
), ApplicationAchievement, ProgressAchievement {
  
  const val TARGET: Int = 10000
  
  override val target : Int = TARGET
  override var current : Int
    get() = count
    set(value) {
      count = value
    }
  
  var count: Int = 0
  
  val initialized: AtomicBoolean = AtomicBoolean(false)
  
  override fun init() {
    initialized.set(true)
  }
  
  override fun dispose() {
  }
}