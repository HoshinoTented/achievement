package com.github.hoshinotented.achievement.util

fun runCommand(commands: Array<String>): Int {
  val process = Runtime.getRuntime().exec(commands)
  return process.waitFor()
}