package com.github.hoshinotented.achievement.achievements.project

import com.github.hoshinotented.achievement.achievements.AbstractAchievement
import com.github.hoshinotented.achievement.achievements.complete
import com.github.hoshinotented.achievement.core.ProgressAchievement
import com.github.hoshinotented.achievement.core.ProjectAchievement
import com.intellij.execution.ExecutionListener
import com.intellij.execution.ExecutionManager
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project
import java.util.concurrent.atomic.AtomicInteger

class BadExecution : AbstractAchievement(
  "project.badExecution",
  false
), ProjectAchievement, ProgressAchievement, ExecutionListener {
  companion object {
    const val TARGET = Int.MAX_VALUE
  }
  
  private val count: AtomicInteger = AtomicInteger(0)
  
  override val target: Int = TARGET
  override var current: Int
    get() = count.get()
    set(value) {
      count.set(value)
    }
  
  override fun processTerminated(
    executorId: String,
    env: ExecutionEnvironment,
    handler: ProcessHandler,
    exitCode: Int
  ) {
    if (exitCode != 0) {
      val newCount = count.incrementAndGet()
      if (newCount == target) complete()
    }
  }
  
  override suspend fun init(project: Project) {
    project.messageBus.connect(this)
      .subscribe(ExecutionManager.EXECUTION_TOPIC, this)
  }
  
  override fun dispose() {
    // messageBus will be automatically disposed
  }
}