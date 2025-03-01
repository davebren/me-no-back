package org.eski.menoback.ui.game.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.eski.menoback.ui.game.model.Tetrimino

class GameNbackViewModel(
  scope: CoroutineScope,
  val gameState: MutableStateFlow<GameState>,
  val currentTetrimino: MutableStateFlow<Tetrimino?>,
) {
  var matchChoiceMade = false; private set

  val level = MutableStateFlow(1)
  val streak = MutableStateFlow(0)

  val multiplier = combine(level, streak) { level, streak ->
    1.0f + (streak * (level * 2) * 0.1f)
  }.stateIn(scope, SharingStarted.Eagerly, 1f)

  val multiplierText = multiplier.map {
    if (!it.toString().contains('.')) "${it}x"
    else "${it.toString().subSequence(0, it.toString().indexOf('.') + 2)}x"
  }.stateIn(scope, SharingStarted.WhileSubscribed(), "1.0x")

  fun increaseLevel() {
    if (gameState.value == GameState.NotStarted && level.value < 15) {
      level.value++
    }
  }

  fun decreaseLevel() {
    if (gameState.value == GameState.NotStarted && level.value > 1) {
      level.value--
    }
  }

  fun matchChoice(tetriminoHistory: List<Tetrimino>) {
    if (gameState.value != GameState.Running || matchChoiceMade) return
    matchChoiceMade = true

    val correct = if (tetriminoHistory.size > level.value) {
      val nBackPiece = tetriminoHistory[tetriminoHistory.size - level.value - 1]
      currentTetrimino.value?.type == nBackPiece.type
    } else {
      false
    }

    streak.value = if (correct) { streak.value + 1 } else 0
  }

  fun noMatchChoice(tetriminoHistory: List<Tetrimino>) {
    if (gameState.value != GameState.Running || matchChoiceMade) return
    matchChoiceMade = true

    val correct = if (tetriminoHistory.size > level.value) {
      val nBackPiece = tetriminoHistory[tetriminoHistory.size - level.value - 1]
      currentTetrimino.value?.type != nBackPiece.type
    } else true

    streak.value = if (correct) { streak.value + 1 } else 0
  }

  fun clearMatchChoice() { matchChoiceMade = false }
}