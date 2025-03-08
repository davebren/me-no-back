package org.eski.menoback.ui.game.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.eski.menoback.data.gameSettings
import org.eski.menoback.ui.game.model.MatchStats
import org.eski.menoback.ui.game.model.NbackStimulus
import org.eski.menoback.ui.game.model.TetriminoHistory
import kotlin.math.max

class GameNbackViewModel(
  val scope: CoroutineScope,
  val gameState: MutableStateFlow<GameState>,
  val currentTetrimino: MutableStateFlow<TetriminoHistory.Entry?>,
) {
  val currentTetriminoMatchChoicesEntered = NbackStimulus.Type.entries.associateWith { false }.toMutableMap()

  val setting = gameSettings.nbackSetting
  val level: StateFlow<Int> = setting.map { it.first().level }
    .stateIn(scope, SharingStarted.WhileSubscribed(), 2)

  val colorNbackEnabled: StateFlow<Boolean> = setting.map {
    it.find { stimulus -> stimulus.type == NbackStimulus.Type.color } != null
  }.stateIn(scope, SharingStarted.WhileSubscribed(), false)


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
    multiplier.value = 1f
  }

  fun matchChoice(tetriminoHistory: List<TetriminoHistory.Entry>, type: NbackStimulus.Type) {
    if (gameState.value != GameState.Running
      || currentTetriminoMatchChoicesEntered[type] ?: throw IllegalArgumentException()) {
      return
    }
    currentTetriminoMatchChoicesEntered[type] = true
    println("matchChoice: ${type.name}")

    val correct = if (tetriminoHistory.size > setting.value.first().level) {
      val nBackPiece = tetriminoHistory[tetriminoHistory.size - setting.value.first().level - 1]
      when(type) {
        NbackStimulus.Type.block -> currentTetrimino.value?.tetrimino?.type == nBackPiece.tetrimino.type
        NbackStimulus.Type.color -> currentTetrimino.value?.colorType == nBackPiece.colorType
      }
    } else false

    val oldStats = matchStats.value

    matchStats.value = when(type) {
      NbackStimulus.Type.block -> oldStats.copy(
        correctShapeMatches = oldStats.correctShapeMatches + if (correct) 1 else 0,
        incorrectShapeMatches = oldStats.incorrectShapeMatches + if (!correct) 1 else 0
      )
      NbackStimulus.Type.color -> oldStats.copy(
        correctColorMatches = oldStats.correctColorMatches + if (correct) 1 else 0,
        incorrectColorMatches = oldStats.incorrectColorMatches + if (correct) 1 else 0
      )
    }

    updateMultiplier(correct)
    streak.value = if (correct) { streak.value + 1 } else 0
    feedback.value = if (correct) FeedbackState.correct else FeedbackState.incorrect

    scope.launch {
      delay(300)
      feedback.value = FeedbackState.none
    }
  }

  fun noMatchChoice(tetriminoHistory: List<TetriminoHistory.Entry>, type: NbackStimulus.Type) {
    if (gameState.value != GameState.Running
      || currentTetriminoMatchChoicesEntered[type] ?: throw IllegalArgumentException()) {
      return
    }
    currentTetriminoMatchChoicesEntered[type] = true

    val correct = if (tetriminoHistory.size > setting.value.first().level) {
      val nBackPiece = tetriminoHistory[tetriminoHistory.size - setting.value.first().level - 1]
      when(type) {
        NbackStimulus.Type.block -> currentTetrimino.value?.tetrimino?.type != nBackPiece.tetrimino.type
        NbackStimulus.Type.color -> currentTetrimino.value?.colorType != nBackPiece.colorType
      }
    } else true

    val oldStats = matchStats.value

    matchStats.value = when(type) {
      NbackStimulus.Type.block -> oldStats.copy(
        correctShapeNonMatches = oldStats.correctShapeNonMatches + if (correct) 1 else 0,
        missedShapeMatches = oldStats.missedShapeMatches + if (!correct) 1 else 0
      )
      NbackStimulus.Type.color -> oldStats.copy(
        correctColorNonMatches = oldStats.correctColorNonMatches + if (correct) 1 else 0,
        missedShapeMatches = oldStats.missedColorMatches + if (!correct) 1 else 0
      )
    }

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

  fun clearMatchChoices() {
    currentTetriminoMatchChoicesEntered.putAll(currentTetriminoMatchChoicesEntered.mapValues { false  })
  }

  fun colorNbackToggled() = gameSettings.toggleNbackStimulus(NbackStimulus.Type.color)

  private fun updateMultiplier(correct: Boolean) {
    return if (!correct) multiplier.value = max(1f, multiplier.value / 2f)
      else multiplier.value += (setting.value.first().level * 0.2f * setting.value.size)
  }

  enum class FeedbackState {
    correct,
    incorrect,
    none,
  }
}