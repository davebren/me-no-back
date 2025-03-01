package org.eski.menoback.ui.game.model

data class Tetrimino(val type: Int, val shape: Array<IntArray>) {

  fun rotate(direction: Rotation): Tetrimino {
    val initialRowCount = shape.size
    val initialColumnCount = shape[0].size
    val transposedShape = Array(initialColumnCount) { IntArray(initialRowCount) }

    when(direction) {
      Rotation.clockwise -> {
        for (row in 0 until initialRowCount) {
          for (col in 0 until initialColumnCount) {
            transposedShape[col][initialRowCount - 1 - row] = shape[row][col]
          }
        }
      }
      Rotation.counterClockwise -> {
        for (row in 0 until initialRowCount) {
          for (col in 0 until initialColumnCount) {
            transposedShape[initialColumnCount - 1 - col][row] = shape[row][col]
          }
        }
      }
    }

    return Tetrimino(type, transposedShape)
  }

  // Override equals and hashCode because we use Array which doesn't implement them properly
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (this::class != other?.let { it::class }) return false

    other as Tetrimino

    if (type != other.type) return false
    if (!shape.contentDeepEquals(other.shape)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = type
    result = 31 * result + shape.contentDeepHashCode()
    return result
  }

  data class Position(val row: Int, val col: Int)

  companion object {
    const val lockedType = 8

    val types = listOf(
      Tetrimino(
        type = 1,
        shape = arrayOf(
          intArrayOf(0, 0, 0, 0),
          intArrayOf(1, 1, 1, 1),
          intArrayOf(0, 0, 0, 0),
          intArrayOf(0, 0, 0, 0)
        )
      ),
      Tetrimino(
        type = 2,
        shape = arrayOf(
          intArrayOf(2, 2),
          intArrayOf(2, 2)
        )
      ),
      Tetrimino(
        type = 3,
        shape = arrayOf(
          intArrayOf(0, 3, 0),
          intArrayOf(3, 3, 3),
          intArrayOf(0, 0, 0)
        )
      ),
      Tetrimino(
        type = 4,
        shape = arrayOf(
          intArrayOf(0, 0, 4),
          intArrayOf(4, 4, 4),
          intArrayOf(0, 0, 0)
        )
      ),
      Tetrimino(
        type = 5,
        shape = arrayOf(
          intArrayOf(5, 0, 0),
          intArrayOf(5, 5, 5),
          intArrayOf(0, 0, 0)
        )
      ),
      Tetrimino(
        type = 6,
        shape = arrayOf(
          intArrayOf(0, 6, 6),
          intArrayOf(6, 6, 0),
          intArrayOf(0, 0, 0)
        )
      ),
      Tetrimino(
        type = 7,
        shape = arrayOf(
          intArrayOf(7, 7, 0),
          intArrayOf(0, 7, 7),
          intArrayOf(0, 0, 0)
        )
      )
    )
  }
}