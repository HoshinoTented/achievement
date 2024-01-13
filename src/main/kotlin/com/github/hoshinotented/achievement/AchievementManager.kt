package com.github.hoshinotented.achievement

import com.github.hoshinotented.achievement.achievement.application.AgdaKeybindAchievement
import com.github.hoshinotented.achievement.achievement.application.TypingAchievement
import com.github.hoshinotented.achievement.achievement.application.WelcomeAchievement
import com.github.hoshinotented.achievement.achievement.application.YuanShenAchievement
import com.github.hoshinotented.achievement.achievement.project.BadExecutionAchievement
import com.github.hoshinotented.achievement.achievement.project.CodeOneDayAchievement
import com.github.hoshinotented.achievement.achievement.project.OpenAyaWithoutAyaPluginAchievement
import com.github.hoshinotented.achievement.core.Achievement
import com.github.hoshinotented.achievement.core.ApplicationAchievement
import com.github.hoshinotented.achievement.core.ProjectAchievement
import com.github.hoshinotented.achievement.notification.AchievementNotification
import com.github.hoshinotented.achievement.service.AchievementData
import com.intellij.ide.DataManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import kala.collection.CollectionView
import kala.collection.mutable.MutableMap
import kala.value.AtomicVar
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean

@Service(Service.Level.APP)
class AchievementManager : Disposable {
  companion object {
    @get:JvmName("getInstance")
    val INSTANCE: AchievementManager
      get() = ApplicationManager.getApplication().getService(AchievementManager::class.java)
  }
  
  enum class State {
    NotInitialized,
    Initializing,
    Initialized
  }
  
  private val achievements: MutableMap<String, Achievement> = MutableMap.create()
  private var managerState: AtomicVar<State> = AtomicVar(State.NotInitialized)
  private val appInitialized: AtomicBoolean = AtomicBoolean(false)
  
  init {
    val achievements = listOf(
      WelcomeAchievement(),
      CodeOneDayAchievement(),
      TypingAchievement,
      YuanShenAchievement(),
      BadExecutionAchievement(),
      OpenAyaWithoutAyaPluginAchievement(),
      AgdaKeybindAchievement
    )
    
    achievements.forEach(::addAchievement)
  }
  
  fun addAchievement(achi: Achievement): Boolean {
    return achievements.putIfAbsent(achi.id, achi).isEmpty
  }
  
  fun findAchievement(id: String): Achievement? {
    return achievements.getOrNull(id)
  }
  
  fun achievements(): CollectionView<Achievement> {
    return achievements.valuesView()
  }
  
  fun projectAchievements(): CollectionView<ProjectAchievement> {
    return achievements().filterIsInstance(ProjectAchievement::class.java)
  }
  
  fun applicationAchievements(): CollectionView<ApplicationAchievement> {
    return achievements().filterIsInstance(ApplicationAchievement::class.java)
  }
  
  private fun loadAchievementData(state: AchievementData.MyState) {
    achievements().forEach {
      val myData = state.data[it.id] ?: return@forEach
      it.deserialize(myData)
    }
  }
  
  private fun doInitialize() {
    val state = AchievementData.INSTANCE.myState
    loadAchievementData(state)
    
    achievements()
      .filterNot { it.isCompleted }
      .forEach {
        Disposer.register(this, it)
      }
  }
  
  /**
   * Initialize [AchievementManager] in order to load necessary resources
   */
  fun initialize() {
    if (managerState.compareAndSet(State.NotInitialized, State.Initializing)) {
      doInitialize()
      managerState.set(State.Initialized)
    }
  }
  
  fun initializeAndWait() {
    initialize()
    
    while (true) {
      if (managerState.get() === State.Initialized) break
    }
  }
  
  fun initializeProjectAchievement(project: Project) {
    initializeAndWait()
    
    runReadAction {
      if (!project.isDisposed) {
        projectAchievements()
          .filterNot { it.isCompleted }
          .forEach {
            it.init(project)
          }
      }
    }
  }
  
  fun initializeApplicationAchievement() {
    initializeAndWait()
    
    if (appInitialized.compareAndSet(false, true)) {
      applicationAchievements()
        .filterNot { it.isCompleted }
        .forEach(ApplicationAchievement::init)
    }
  }
  
  /**
   * Mark the achievement as complete, report to user, and dispose the achievement.
   * Note that this function is not thread-safe.
   */
  fun completeAchievement(achi: Achievement) {
    if (!achi.isCompleted) {
      achi.isCompleted = true
      Disposer.dispose(achi)
      
      val notification = AchievementNotification.achievement
        .createNotification(achi.name, achi.description, NotificationType.INFORMATION)
      try {
        DataManager.getInstance().dataContextFromFocusAsync
          .then {
            val proj = CommonDataKeys.PROJECT.getData(it)
            notification.notify(proj)
          }.blockingGet(1, TimeUnit.SECONDS)
      } catch (e: TimeoutException) {
        AchievementPlugin.LOG.error(e)
      }
      
      AchievementPlugin.LOG.info("Achievement ${achi.id} complete.")
    } else {
      AchievementPlugin.LOG.warn("Achievement ${achi.id} complete twice, something is wrong.")
    }
  }
  
  override fun dispose() {
  }
}