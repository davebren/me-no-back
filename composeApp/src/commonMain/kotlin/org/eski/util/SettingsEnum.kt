package org.eski.util

import kotlin.enums.enumEntries

interface SettingsEnum {
  val stableId: Int
}

inline fun <reified T : Enum<T>> enumFromStableId(stableId: Int): T {
  enumEntries<T>().forEach {
    if (it is SettingsEnum && it.stableId == stableId) return it
  }
  throw IllegalArgumentException()
}