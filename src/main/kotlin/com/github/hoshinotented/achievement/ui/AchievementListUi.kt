package com.github.hoshinotented.achievement.ui

import com.github.hoshinotented.achievement.AchievementMain
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants

class AchievementListUi : Configurable {
  override fun createComponent() : JComponent {
    val list = JPanel()
    list.layout = BoxLayout(list, BoxLayout.Y_AXIS)
    
    AchievementMain.achievements.view()
      .filter { (!it.isHidden) || it.isCompleted }
      .forEach {
        val elem = AchievementElement().initialize(it)
        list.add(elem.panel)
      }
    
    return JBScrollPane(
      list,
      ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
    )
  }
  
  override fun isModified() : Boolean = false
  
  override fun apply() = Unit
  
  override fun getDisplayName() : String = "Achievements"
}