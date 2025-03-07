package org.eski.util

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


inline fun <reified  T> String.safeJsonDecode(): T? {
  try {
    return Json.decodeFromString<T>(this)
  } catch (error: SerializationException) {
    // TODO: Add debug logs.
  } catch (error: IllegalArgumentException) {
    // TODO: Add debug logs.
  }
  return null
}

inline fun <reified T> T.safeJsonEncode(): String? {
  try {
    return Json.encodeToString(this)
  } catch (error: SerializationException) {
    // TODO: Add debug logs.
  }
  return null
}