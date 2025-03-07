package org.eski.menoback.ui.game.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.eski.menoback.data.gameSettings
import org.eski.menoback.data.settings
import org.eski.menoback.ui.game.model.MatchStats
import org.eski.menoback.ui.game.model.Tetrimino
import kotlin.math.max

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
  val matchStats = MutableStateFlow(MatchStats())

  val feedback = MutableStateFlow<FeedbackState>(FeedbackState.none)

  val multiplier = MutableStateFlow<Float>(1f)

  val multiplierText = multiplier.map {
    if (!it.toString().contains('.')) "${it}x"
    else "${it.toString().subSequence(0, it.toString().indexOf('.') + 2)}x"
  }.stateIn(scope, SharingStarted.WhileSubscribed(), "1.0x")

  fun increaseLevel() { gameSettings.increaseNbackLevel() }
  fun decreaseLevel() { gameSettings.decreaseNbackLevel() }

  fun reset() {
    matchStats.value = MatchStats()
    streak.value = 0
  }

  // TODO: Support multiple stimuli.
  fun matchChoice(tetriminoHistory: List<Tetrimino>) {
    if (gameState.value != GameState.Running || matchChoiceMade) return
    matchChoiceMade = true

    val correct = if (tetriminoHistory.size > setting.value.first().level) {
      val nBackPiece = tetriminoHistory[tetriminoHistory.size - setting.value.first().level - 1]
      currentTetrimino.value?.type == nBackPiece.type
    } else false

    val oldStats = matchStats.value
    matchStats.value = oldStats.copy(
      correctMatches = oldStats.correctMatches + if (correct) 1 else 0,
      incorrectMatches = oldStats.incorrectMatches + if (!correct) 1 else 0
    )

    updateMultiplier(correct)
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

    val oldStats = matchStats.value
    matchStats.value = oldStats.copy(
      correctNonMatches = oldStats.correctNonMatches + if (correct) 1 else 0,
      missedMatches = oldStats.incorrectMatches + if (!correct) 1 else 0
    )

    updateMultiplier(correct)
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

  private fun updateMultiplier(correct: Boolean) {
    return if (!correct) multiplier.value = max(1f, multiplier.value / 2f)
      else multiplier.value += (setting.value.first().level * 0.2f)
  }

  enum class FeedbackState {
    correct,
    incorrect,
    none,
  }
}