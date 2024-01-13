package com.github.hoshinotented.achievement.achievement.project

import com.github.hoshinotented.achievement.core.ProjectAchievement
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project

interface FileOpenAchievement : ProjectAchievement {
  val handler: FileEditorManagerListener
  
  override fun init(project: Project) {
    project.messageBus.connect(this)
      .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, handler)
  }
}