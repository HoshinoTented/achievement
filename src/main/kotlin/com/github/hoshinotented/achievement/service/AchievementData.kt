package com.github.hoshinotented.achievement.service

import com.github.hoshinotented.achievement.AchievementManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
  name = "com.github.hoshinotented.achievement.services.AchievementData",
  storages = [Storage("achievementData.xml")]
)
class AchievementData : PersistentStateComponent<AchievementData.MyState> {
  companion object {
    val INSTANCE : AchievementData get() = ApplicationManager.getApplication().getService(AchievementData::class.java)
    const val KEY_COMPLETED : String = "isCompleted"
    const val KEY_PROGRESS : String = "progress"
  }
  
  class MyState {
    var data : MutableMap<String, MutableMap<String, String>> = HashMap()
  }
  
  var myState : MyState = MyState()
  
  /// region override
  
  override fun getState() : MyState {
    val newState = MyState()
    val manager = AchievementManager.INSTANCE
    // on save
    // collect data
    manager.achievements().forEach {
      val innerData = HashMap<String, String>()
      it.serialize(innerData)
      
      newState.data[it.id] = innerData
    }
    
    return newState
  }
  
  override fun loadState(state : MyState) {
    XmlSerializerUtil.copyBean(state, this.myState)
  }
  
  /// endregion override
}