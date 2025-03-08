package org.eski.menoback.ui.game.model

import kotlin.random.Random


enum class NbackTetriminoColor(val colorIndex: Int) {
  one(1),
  two(2),
  three(3),
  four(4),
  five(5),
  six(6),
  seven(7);

  companion object {
    fun fromIndex(index: Int): NbackTetriminoColor =
      entries.find { it.colorIndex == index } ?: one

    fun random(): NbackTetriminoColor {
      return fromIndex(Random.nextInt(1, 8))
    }
  }
}