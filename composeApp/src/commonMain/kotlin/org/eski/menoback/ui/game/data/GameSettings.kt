package org.eski.menoback.ui.game.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameSettings(val settings: Settings) {
  companion object {
    private const val settingsKey = "settings.game"
    private const val gameDurationKey = "$settingsKey.duration"

    // Available game durations in seconds
    val AVAILABLE_DURATIONS = listOf(30, 60, 120, 300, 600, 1800)

    // Default game duration (60 seconds)
    private const val defaultGameDuration = 60
  }

  // StateFlow for game duration
  private val _gameDuration = MutableStateFlow(settings.getInt(gameDurationKey, defaultGameDuration))
  val gameDuration = _gameDuration.asStateFlow()

  // Function to update game duration
  fun setGameDuration(durationSeconds: Int) {
    if (AVAILABLE_DURATIONS.contains(durationSeconds)) {
      _gameDuration.value = durationSeconds
      settings.putInt(gameDurationKey, durationSeconds)
    }
  }

  // Function to increase game duration to next available option
  fun increaseGameDuration() {
    val currentDuration = _gameDuration.value
    val nextDuration = AVAILABLE_DURATIONS.find { it > currentDuration } ?: currentDuration
    setGameDuration(nextDuration)
  }

  // Function to decrease game duration to previous available option
  fun decreaseGameDuration() {
    val currentDuration = _gameDuration.value
    val prevDuration = AVAILABLE_DURATIONS.findLast { it < currentDuration } ?: currentDuration
    setGameDuration(prevDuration)
  }

  // Format duration for display
  fun formatDuration(durationSeconds: Int): String {
    return when {
      durationSeconds <= 60 -> "${durationSeconds}s"
      durationSeconds % 60 == 0 -> "${durationSeconds / 60}m"
      else -> "${durationSeconds / 60}m ${durationSeconds % 60}s"
    }
  }

  // Reset to default
  fun resetToDefault() {
    setGameDuration(defaultGameDuration)
  }
}