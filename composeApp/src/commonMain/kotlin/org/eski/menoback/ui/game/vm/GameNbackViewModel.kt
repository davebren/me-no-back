package org.eski.menoback.ui.game.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.eski.menoback.data.gameSettings
import org.eski.menoback.data.gameStatsData
import org.eski.menoback.ui.game.data.GameStatsData
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

  // Add the max level from the game settings
  val maxLevel: StateFlow<Int> = gameSettings.currentMaxLevel
    .stateIn(scope, SharingStarted.WhileSubscribed(), 2)

  val shapeNbackEnable: StateFlow<Boolean> = setting.map {
    it.find { stimulus -> stimulus.type == NbackStimulus.Type.shape} != null
  }.stateIn(scope, SharingStarted.WhileSubscribed(), false)

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

  val showLevelUnlocked = MutableStateFlow<Boolean>(false)

  val maxLevelText: StateFlow<String> = gameSettings.gameDurationFormatted.map { duration ->
    "Play at this level for ${duration} with ${GameStatsData.accuracyThreshold}%+ accuracy to unlock the next level."
 }.stateIn(scope, SharingStarted.WhileSubscribed(), "")

  init {
    scope.launch {
      gameSettings.currentMaxLevel.collectLatest { maxLevel ->
        if (level.value > maxLevel) setLevel(maxLevel)
      }
    }
  }

  fun increaseLevel() {
    // Only allow increase if current level is below max unlocked level
    if (level.value < maxLevel.value) {
      gameSettings.increaseNbackLevel()
    }
  }

  fun decreaseLevel() { gameSettings.decreaseNbackLevel() }

  private fun setLevel(level: Int) { gameSettings.setNbackLevel(level) }

  fun reset() {
    matchStats.value = MatchStats()
    streak.value = 0
    multiplier.value = 1f
  }

  /**
   * Called at the end of a game to check if progression was achieved
   * Returns true if a new level was unlocked
   */
  fun checkLevelProgression(): Boolean {
    val totalDecisionsRequired = gameSettings.gameDuration.value / 3
    if (matchStats.value.totalDecisions < totalDecisionsRequired) return false

    val accuracy = matchStats.value.accuracyPercentage
    if (accuracy >= GameStatsData.accuracyThreshold) {
      val currentMaxLevel = gameStatsData.unlockedLevel(gameSettings.gameDuration.value, setting.value)
      if (currentMaxLevel == level.value && level.value < 15) {
        gameStatsData.putUnlockedLevel(currentMaxLevel + 1, gameSettings.gameDuration.value, setting.value)
        showLevelUnlocked.value = true
        return true
      }
    }
    showLevelUnlocked.value = false
    return false
  }

  fun matchChoice(tetriminoHistory: List<TetriminoHistory.Entry>, type: NbackStimulus.Type) {
    if (gameState.value != GameState.Running
      || currentTetriminoMatchChoicesEntered[type] ?: throw IllegalArgumentException()) {
      return
    }
    currentTetriminoMatchChoicesEntered[type] = true

    val correct = if (tetriminoHistory.size > setting.value.first().level) {
      val nBackPiece = tetriminoHistory[tetriminoHistory.size - setting.value.first().level - 1]
      when(type) {
        NbackStimulus.Type.shape -> currentTetrimino.value?.tetrimino?.type == nBackPiece.tetrimino.type
        NbackStimulus.Type.color -> currentTetrimino.value?.colorType == nBackPiece.colorType
      }
    } else false

    val oldStats = matchStats.value

    matchStats.value = when(type) {
      NbackStimulus.Type.shape -> oldStats.copy(
        correctShapeMatches = oldStats.correctShapeMatches + if (correct) 1 else 0,
        incorrectShapeMatches = oldStats.incorrectShapeMatches + if (!correct) 1 else 0
      )
      NbackStimulus.Type.color -> oldStats.copy(
        correctColorMatches = oldStats.correctColorMatches + if (correct) 1 else 0,
        incorrectColorMatches = oldStats.incorrectColorMatches + if (!correct) 1 else 0
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
        NbackStimulus.Type.shape -> currentTetrimino.value?.tetrimino?.type != nBackPiece.tetrimino.type
        NbackStimulus.Type.color -> currentTetrimino.value?.colorType != nBackPiece.colorType
      }
    } else true

    val oldStats = matchStats.value

    matchStats.value = when(type) {
      NbackStimulus.Type.shape -> oldStats.copy(
        correctShapeNonMatches = oldStats.correctShapeNonMatches + if (correct) 1 else 0,
        missedShapeMatches = oldStats.missedShapeMatches + if (!correct) 1 else 0
      )
      NbackStimulus.Type.color -> oldStats.copy(
        correctColorNonMatches = oldStats.correctColorNonMatches + if (correct) 1 else 0,
        missedColorMatches = oldStats.missedColorMatches + if (!correct) 1 else 0
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

  fun onGameStarted() {
    showLevelUnlocked.value = false
  }

  enum class FeedbackState {
    correct,
    incorrect,
    none,
  }
}