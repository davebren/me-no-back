package org.eski.menoback.ui.game.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.eski.menoback.data.gameSettings
import org.eski.menoback.ui.game.model.Tetrimino

class GameNbackViewModel(
  val scope: CoroutineScope,
  val gameState: MutableStateFlow<GameState>,
  val currentTetrimino: MutableStateFlow<Tetrimino?>,
) {
  var matchChoiceMade = false; private set

  val setting = gameSettings.nbackSetting
  val level: StateFlow<Int> = setting.map { it.first().level }
    .stateIn(scope, SharingStarted.WhileSubscribed(), 2)

  val streak = MutableStateFlow(0)

  val feedback = MutableStateFlow<FeedbackState>(FeedbackState.none)

  val multiplier = combine(setting, streak) { setting, streak ->
    1.0f + (streak * (setting.first().level * 2) * 0.1f) // TODO: Use all stimuli.
  }.stateIn(scope, SharingStarted.Eagerly, 1f)

  val multiplierText = multiplier.map {
    if (!it.toString().contains('.')) "${it}x"
    else "${it.toString().subSequence(0, it.toString().indexOf('.') + 2)}x"
  }.stateIn(scope, SharingStarted.WhileSubscribed(), "1.0x")

  fun increaseLevel() { gameSettings.increaseNbackLevel() }
  fun decreaseLevel() { gameSettings.decreaseNbackLevel() }

  // TODO: Support multiple stimuli.
  fun matchChoice(tetriminoHistory: List<Tetrimino>) {
    if (gameState.value != GameState.Running || matchChoiceMade) return
    matchChoiceMade = true

    val correct = if (tetriminoHistory.size > setting.value.first().level) {
      val nBackPiece = tetriminoHistory[tetriminoHistory.size - setting.value.first().level - 1]
      currentTetrimino.value?.type == nBackPiece.type
    } else {
      false
    }

    streak.value = if (correct) { streak.value + 1 } else 0

    feedback.value = if (correct) FeedbackState.correct else FeedbackState.incorrect
    scope.launch {
      delay(300)
      feedback.value = FeedbackState.none
    }
  }

  // TODO: Support multiple stimuli.
  fun noMatchChoice(tetriminoHistory: List<Tetrimino>) {
    if (gameState.value != GameState.Running || matchChoiceMade) return
    matchChoiceMade = true

    val correct = if (tetriminoHistory.size > setting.value.first().level) {
      val nBackPiece = tetriminoHistory[tetriminoHistory.size - setting.value.first().level - 1]
      currentTetrimino.value?.type != nBackPiece.type
    } else true

    streak.value = if (correct) { streak.value + 1 } else 0

    if (!correct) {
      feedback.value = FeedbackState.incorrect
      scope.launch {
        delay(300)
        feedback.value = FeedbackState.none
      }
    }
  }

  fun clearMatchChoice() { matchChoiceMade = false }

  enum class FeedbackState {
    correct,
    incorrect,
    none,
  }
}