package com.github.hoshinotented.achievement.achievements.project

import com.github.hoshinotented.achievement.achievements.AbstractAchievement
import com.github.hoshinotented.achievement.achievements.complete
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile

class OpenAyaWithoutAyaPluginAchievement : AbstractAchievement("project.aya", true), FileOpenAchievement {
  companion object {
    val AYA_EXTENSIONS = arrayOf("aya", "aya.md")
    val PLUGIN_ID_AYA = PluginId.findId("org.aya.intellij")
  }
  
  override val handler: FileEditorManagerListener = object : FileEditorManagerListener {
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
      val isAyaEnabled = PluginManagerCore.getPlugin(PLUGIN_ID_AYA)?.isEnabled == true
      val ext = file.extension ?: return
      
      if ((!isAyaEnabled) && AYA_EXTENSIONS.any { it == ext }) {
        complete()
      }
    }
  }
  
  override fun dispose() {
  }
}