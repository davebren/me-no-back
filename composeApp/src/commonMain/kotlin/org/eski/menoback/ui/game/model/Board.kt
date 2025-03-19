package org.eski.menoback.ui.game.model

import org.eski.util.deepCopy
import kotlin.random.Random

const val boardWidth = 10
const val boardHeight = 20
val newTetriminoStartPosition = Tetrimino.Position(row = 0, col = boardWidth / 2 - 2)

data class Board(
  val matrix: Array<IntArray> = Array(boardHeight) { IntArray(boardWidth) { 0 } }
) {

  fun copy(updates: Map<Int, Map<Int, Int>>?): Board {
    val newMatrix = Array(boardHeight) { row ->
      IntArray(boardWidth) { column ->
        updates?.get(row)?.get(column) ?: matrix[row][column]
      }
    }
    return Board(newMatrix)
  }

  fun with(newTetrimino: Tetrimino, position: Tetrimino.Position): Board {
    val newMatrix = matrix.deepCopy()
    
    for (row in newTetrimino.shape.indices) {
      for (col in newTetrimino.shape[row].indices) {
        if (newTetrimino.shape[row][col] != 0) {
          val boardRow = position.row + row
          val boardCol = position.col + col

          if (boardRow >= 0 && boardRow < newMatrix.size &&
            boardCol >= 0 && boardCol < newMatrix[0].size
          ) {
            newMatrix[boardRow][boardCol] = newTetrimino.type
          }
        }
      }
    }
    return Board(newMatrix)
  }

  fun addDigRows(newDigRows: Int): Board {
    val newMatrix = Array(boardHeight) {
      if (it < (boardHeight - newDigRows)) {
        matrix[it + newDigRows].copyOf()
      } else IntArray(boardWidth)
    }

    for (rowIndex in boardHeight - (newDigRows + 1) until boardHeight) {
      val missingSquare = ((lastDigMissingSquare ?: 0) + Random.nextInt(boardWidth - 1)) % boardWidth
      lastDigMissingSquare = missingSquare
      for (columnIndex in 0 until boardWidth) {
        if (columnIndex != missingSquare) newMatrix[rowIndex][columnIndex] = Tetrimino.lockedType
      }
    }

    return Board(newMatrix)
  }

  fun validPosition(tetrimino: Tetrimino?, position: Tetrimino.Position): Boolean {
    if (tetrimino == null) return false

    for (row in tetrimino.shape.indices) {
      for (col in tetrimino.shape[row].indices) {
        if (tetrimino.shape[row][col] != 0) {
          val boardRow = position.row + row
          val boardCol = position.col + col

          // Check bounds
          if (boardRow < 0 || boardRow >= boardHeight ||
            boardCol < 0 || boardCol >= boardWidth
          ) {
            return false
          }

          // Check collision with existing blocks
          if (matrix[boardRow][boardCol] != 0) {
            return false
          }
        }
      }
    }

    return true
  }

  private fun printBoard(matrix: Array<IntArray>) {
    matrix.forEach { println(it.joinToString(" ")) }
  }

  companion object {
    var lastDigMissingSquare: Int? = null
  }
}
