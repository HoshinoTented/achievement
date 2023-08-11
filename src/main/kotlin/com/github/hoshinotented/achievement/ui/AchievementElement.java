package com.github.hoshinotented.achievement.ui;

import com.github.hoshinotented.achievement.core.Achievement;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AchievementElement {
  private JLabel labelComplete;
  private JLabel labelName;
  private JLabel labelDescription;
  private JPanel panelMain;

  public @NotNull AchievementElement initialize(@NotNull Achievement achi) {
    var color = achi.isCompleted() ? JBColor.GREEN : JBColor.RED;

    labelComplete.setForeground(color);
    labelComplete.setText(achi.getProgress());
    labelName.setText(achi.getName());
    labelDescription.setText(achi.getDescription());

    return this;
  }

  public @NotNull JPanel getPanel() {
    return panelMain;
  }
}
