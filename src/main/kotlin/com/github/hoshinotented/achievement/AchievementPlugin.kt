package com.github.hoshinotented.achievement

import com.intellij.openapi.diagnostic.Logger
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext

object AchievementPlugin {
  val LOG = Logger.getInstance(AchievementPlugin::class.java)
  
  val SCOPE: CoroutineScope = CoroutineScope(EmptyCoroutineContext)
}