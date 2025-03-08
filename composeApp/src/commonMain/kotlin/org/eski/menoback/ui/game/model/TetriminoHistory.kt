package org.eski.menoback.ui.game.model

class TetriminoHistory {
  val entries = mutableListOf<Entry>()

  fun clear() = entries.clear()

  fun add(tetrimino: Tetrimino, colorType: NbackTetriminoColor? = null) = entries.add(Entry(tetrimino, colorType))

  data class Entry(val tetrimino: Tetrimino, val colorType: NbackTetriminoColor?)
}