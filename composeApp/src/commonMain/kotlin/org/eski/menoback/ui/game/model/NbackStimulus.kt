package org.eski.menoback.ui.game.model

import kotlinx.serialization.Serializable
import org.eski.util.SettingsEnum

@Serializable
data class NbackStimulus(val type: Type, val level: Int) {
  @Serializable
  enum class Type(override val stableId: Int): SettingsEnum {
    shape(0), color(1)
  }

  fun settingsKey() = "type-${type.stableId}:level-$level"
}