package org.eski.menoback.ui

import androidx.compose.ui.graphics.Color
import org.eski.menoback.ui.game.model.Tetrimino

data class TetriminoColors(
  val one: Color = Color(0xff90d8f9),
  val two: Color = Color(0xfffdfd96),
  val three: Color = Color(0xffffa3e2),
  val four: Color = Color(0xff6699ff),
  val five: Color = Color(0xffffcb94),
  val six: Color = Color(0xffa9e5a9),
  val seven: Color = Color(0xffda5d5d),
  val locked: Color = Color.LightGray,
) {
  fun fromInt(value: Int): Color? = when(value) {
    0 -> null
    1 -> one
    2 -> two
    3 -> three
    4 -> four
    5 -> five
    6 -> six
    7 -> seven
    Tetrimino.lockedType -> locked
    else -> null
  }
}