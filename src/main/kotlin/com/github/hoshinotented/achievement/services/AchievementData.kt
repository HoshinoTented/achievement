package com.github.hoshinotented.achievement.services

import com.github.hoshinotented.achievement.core.Achievement
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
  name = "com.github.hoshinotented.achievement.services.AchievementData",
  storages = [Storage("achievementData.xml")]
)
class AchievementData : PersistentStateComponent<AchievementData> {
  companion object {
    val INSTANCE : AchievementData get() = ApplicationManager.getApplication().getService(AchievementData::class.java)
    const val KEY_COMPLETED : String = "isCompleted"
  }
  
  val data : MutableMap<String, MutableMap<String, String>> = HashMap()
  
  fun complete(ache : Achievement) {
    val acheData = data.getOrElse(ache.id) { HashMap() }
    acheData[KEY_COMPLETED] = true.toString()
    data[ache.id] = acheData
  }
  
  fun isComplete(ache : Achievement) : Boolean {
    return data[ache.id]?.get(KEY_COMPLETED)?.toBooleanStrictOrNull() ?: false
  }
  
  /// region override
  
  override fun getState() : AchievementData {
    return this
  }
  
  override fun loadState(state : AchievementData) {
    XmlSerializerUtil.copyBean(state, this)
  }
  
  /// endregion override
}