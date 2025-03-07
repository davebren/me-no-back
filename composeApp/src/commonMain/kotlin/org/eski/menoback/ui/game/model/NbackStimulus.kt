package org.eski.menoback.ui.game.model

import kotlinx.serialization.Serializable

@Serializable
data class NbackStimulus(val type: Type, val level: Int) {
  @Serializable
  enum class Type {
    block, color
  }

  override fun toString() = "${type.name}:$level"
}