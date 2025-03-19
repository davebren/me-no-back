package org.eski.menoback.ui.game.model

import kotlinx.serialization.Serializable
import org.eski.util.SettingsEnum

@Serializable
enum class FeedbackMode(override val stableId: Int): SettingsEnum {
  flashBackground(1),
  icon(2);

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