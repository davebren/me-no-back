package org.eski.menoback.ui.game.model

import org.eski.util.SettingsEnum

enum class FeedbackMode(override val stableId: Int): SettingsEnum {
  none(0),
  flashBackground(1);

  companion object {
    val default = flashBackground

    fun fromStableId(stableId: Int): FeedbackMode {
      FeedbackMode.entries.forEach { mode ->
        if (mode.stableId == stableId) return mode
      }
      throw IllegalArgumentException()
    }
  }
}