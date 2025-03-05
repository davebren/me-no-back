package org.eski.menoback.ui.game.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import org.eski.menoback.ui.game.model.FeedbackMode
import org.eski.menoback.ui.game.model.GameDuration
import org.eski.util.enumFromStableId

class GameSettings(val settings: Settings) {
  companion object {
    private const val settingsKey = "settings.game"
    private const val gameDurationKey = "$settingsKey.duration"
    private const val feedbackModeKey = "$settingsKey.feedbackMode"
  }

  val gameDuration = MutableStateFlow(settings.getInt(gameDurationKey, GameDuration.default.durationSeconds))
  val feedbackMode = MutableStateFlow(enumFromStableId<FeedbackMode>(settings.getInt(feedbackModeKey, FeedbackMode.default.stableId)))
  val nbackLevel = MutableStateFlow(2)

  fun setGameDuration(durationSeconds: Int) {
    gameDuration.value = durationSeconds
    settings.putInt(gameDurationKey, durationSeconds)
  }
  fun increaseGameDuration() {
    val currentDuration = gameDuration.value
    val nextDuration = GameDuration.entries.find { it.durationSeconds > currentDuration }?.durationSeconds ?: currentDuration
    setGameDuration(nextDuration)
  }
  fun decreaseGameDuration() {
    val currentDuration = gameDuration.value
    val nextDuration = GameDuration.entries.reversed().find { it.durationSeconds < currentDuration }?.durationSeconds ?: currentDuration
    setGameDuration(nextDuration)
  }
  // TODO: Move to viewmodel.
  fun formatDuration(durationSeconds: Int): String {
    return when {
      durationSeconds <= 60 -> "${durationSeconds}s"
      durationSeconds % 60 == 0 -> "${durationSeconds / 60}m"
      else -> "${durationSeconds / 60}m ${durationSeconds % 60}s"
    }
  }

  fun setFeedbackMode(feedbackMode: FeedbackMode) {
    this.feedbackMode.value = feedbackMode
    settings.putInt(feedbackModeKey, feedbackMode.stableId)
  }
}