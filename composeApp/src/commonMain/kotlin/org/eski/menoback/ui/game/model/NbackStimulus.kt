package org.eski.menoback.ui.game.model

data class NbackStimulus(val type: Type, val level: Int) {
  enum class Type {
    block, color
  }

  override fun toString() = "${type.name}:$level"
}