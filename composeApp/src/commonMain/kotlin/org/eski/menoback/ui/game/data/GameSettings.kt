package org.eski.menoback.ui.game.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.eski.menoback.ui.game.model.FeedbackMode
import org.eski.menoback.ui.game.model.GameDuration
import org.eski.menoback.ui.game.model.NbackStimulus
import org.eski.util.enumFromStableId
import org.eski.util.safeJsonDecode
import org.eski.util.safeJsonEncode
import kotlin.math.max
import kotlin.math.min

class GameSettings(val settings: Settings, val nbackProgress: NbackProgressData) {
  companion object {
    private const val settingsKey = "settings.game"
    private const val gameDurationKey = "$settingsKey.duration"
    private const val feedbackModeKey = "$settingsKey.feedbackMode"
    private const val nbackSettingKey = "$settingsKey.nback"
    private const val showGameControlsKey = "$settingsKey.showGameControls"
  }

  val gameDuration = MutableStateFlow(settings.getInt(gameDurationKey, GameDuration.default.durationSeconds))
  val feedbackMode = MutableStateFlow(enumFromStableId<FeedbackMode>(settings.getInt(feedbackModeKey, FeedbackMode.default.stableId)))
  val nbackSetting = MutableStateFlow<List<NbackStimulus>>(listOf(NbackStimulus(NbackStimulus.Type.shape, 2)))
  val showGameControls = MutableStateFlow(settings.getBoolean(showGameControlsKey, true))

  private val _currentMaxLevel = MutableStateFlow(2)
  val currentMaxLevel = _currentMaxLevel.asStateFlow()

  init {
    val nbackSettingsJson = settings.getStringOrNull(nbackSettingKey)
    nbackSettingsJson?.let { jsonString ->
      val decodedSetting = jsonString.safeJsonDecode<List<NbackStimulus>>()
      decodedSetting?.let { nbackSetting.value = it }
    }
  }

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

  fun toggleNbackStimulus(stimulusType: NbackStimulus.Type) {
    val oldSetting = nbackSetting.value
    val enabled = oldSetting.find { it.type == stimulusType } != null
    if (nbackSetting.value.size == 1 && enabled) return

    val level = oldSetting.first().level

    if (oldSetting.find { it.type == stimulusType } != null) {
      nbackSetting.value = oldSetting.filterNot { it.type == stimulusType }
    } else {
      nbackSetting.value = oldSetting.toMutableList().apply { add(NbackStimulus(stimulusType, level)) }
    }
    saveNbackSetting()
  }

  fun increaseNbackLevel() {
    nbackSetting.value = nbackSetting.value.map { it.copy(level = min(15, it.level + 1)) }
    saveNbackSetting()
  }

  fun decreaseNbackLevel() {
    nbackSetting.value = nbackSetting.value.map { it.copy(level = max(1, it.level - 1)) }
    saveNbackSetting()
  }

  fun setShowGameControls(show: Boolean) {
    showGameControls.value = show
    settings.putBoolean(showGameControlsKey, show)
  }

  private fun saveNbackSetting() {
    nbackSetting.value.safeJsonEncode()?.let { settings.putString(nbackSettingKey, it) }
  }


  fun isCurrentLevelMaxUnlocked(): Boolean {
    return nbackSetting.value.first().level >= currentMaxLevel.value
  }

  /**
   * Updates the max level allowed based on current game settings
   */
  private fun updateCurrentMaxLevel() {
    _currentMaxLevel.value = nbackProgress.getMaxUnlockedLevel(
      gameDuration.value,
      nbackSetting.value
    )
  }

  fun updateNbackProgress(accuracy: Float): Boolean {
    val level = nbackSetting.value.first().level
    val duration = gameDuration.value
    val stimuli = nbackSetting.value

    val newLevelUnlocked = nbackProgress.updateProgress(level, accuracy, duration, stimuli)
    if (newLevelUnlocked) {
      updateCurrentMaxLevel()
    }
    return newLevelUnlocked
  }
}