package org.eski.menoback.ui.game.vm

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.eski.menoback.ui.AppColors
import org.eski.menoback.ui.game.model.Board
import org.eski.menoback.ui.game.model.Tetrimino
import org.eski.menoback.ui.game.model.boardHeight
import org.eski.menoback.ui.game.model.boardWidth
import org.eski.menoback.ui.game.model.newTetriminoStartPosition
import org.eski.menoback.ui.TetriminoColors
import org.eski.menoback.ui.game.achievements.AchievementsData
import org.eski.menoback.ui.game.achievements.AchievementsViewModel
import org.eski.menoback.ui.game.model.Rotation
import org.eski.menoback.ui.game.data.GameSettings
import org.eski.menoback.ui.game.data.GameStatsData
import org.eski.menoback.ui.game.model.NbackStimulus
import org.eski.menoback.ui.game.model.NbackTetriminoColor
import org.eski.menoback.ui.game.model.TetriminoHistory
import org.eski.menoback.data.gameStatsData as defaultGameStatsData
import org.eski.menoback.data.achievementsData as defaultAchievementsData
import org.eski.util.deepCopy
import org.eski.util.equalsIgnoreOrder
import org.eski.util.launchCollect
import org.eski.util.launchCollectLatest
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

const val nbackBaseMatchChance = 0.2f
const val nbackMatchChanceGrowthFactor = 0.1f
const val nbackMatchChanceDecayFactor = 0.05f
const val initialGameTickRate = 1000L
const val fastestTickRate = 200L

class GameScreenViewModel(
  private val gameSettings: GameSettings,
  private val gameStatsData: GameStatsData = defaultGameStatsData,
  private val achievementsData: AchievementsData = defaultAchievementsData,
) : ViewModel() {
  val options = GameOptionsViewModel(viewModelScope, this)
  val valueForValue = ValueForValueViewModel(viewModelScope, this)

  val appColors = MutableStateFlow(AppColors())
  val tetriminoColors = MutableStateFlow(TetriminoColors())
  val achievements = AchievementsViewModel(viewModelScope, gameStatsData, achievementsData)

  private val _gameState = MutableStateFlow<GameState>(GameState.NotStarted)
  val gameState: StateFlow<GameState> = _gameState.asStateFlow()

  val showGameControls: StateFlow<Boolean> = combine(gameSettings.showGameControls, _gameState) {
    showSetting, gameState ->
    gameState == GameState.Running && showSetting
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

  val startButtonVisible = combine(valueForValue.menuShowing, gameState) { valueMenuShowing, gameState ->
    !valueMenuShowing
        && (gameState == GameState.Paused || gameState == GameState.NotStarted || gameState == GameState.GameOver)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)
  val startButtonClickable = gameState.map {
    it == GameState.GameOver || it == GameState.NotStarted || it == GameState.Paused
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

  val currentTetrimino = MutableStateFlow<TetriminoHistory.Entry?>(null)
  val currentPiecePosition = MutableStateFlow<Tetrimino.Position?>(null)
  val nextTetriminos = MutableStateFlow<List<TetriminoHistory.Entry>>(emptyList())
  val nextTetrimino = nextTetriminos.map {
    it.firstOrNull()
  }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
  private val tetriminoHistory = TetriminoHistory()

  val board = MutableStateFlow(Board())
  val displayBoard: StateFlow<Board> = combine(board, currentTetrimino, currentPiecePosition) {
      board: Board, current: TetriminoHistory.Entry?, position: Tetrimino.Position? ->
    if (current == null || position == null) board
    else board.with(current.tetrimino, position)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Board())

  val nback = GameNbackViewModel(viewModelScope, _gameState, currentTetrimino)
  val comboStreak = MutableStateFlow<Int>(0)
  val score = MutableStateFlow<Long>(0)
  private val gameSpeed = MutableStateFlow(initialGameTickRate)

  private var gameJob: Job? = null
  private val gameScope = CoroutineScope(Dispatchers.Default)

  val timeRemaining = MutableStateFlow(gameSettings.gameDuration.value)
  val timerColor = timeRemaining.map {
    val warningThreshold = gameSettings.gameDuration.value / 6
    if (it < warningThreshold) Color.Red else Color.LightGray
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Color.LightGray)

  val currentHighScore: StateFlow<Long> = combine(gameSettings.gameDuration, gameSettings.nbackSetting, gameStatsData.lastScoreUpdate) {
    duration, nback, lastScoreUpdate ->
    if (lastScoreUpdate?.durationSeconds == duration && lastScoreUpdate.nback.equalsIgnoreOrder(nback)) {
      lastScoreUpdate.score
    } else gameStatsData.highScore(duration, nback)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)
  val currentHighScoreText = currentHighScore.map { "High score: $it"}
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

  val feedbackMode = gameSettings.feedbackMode

  private var timerJob: Job? = null

  init {
    gameSettings.gameDuration.launchCollect(viewModelScope) { duration ->
      if (_gameState.value == GameState.NotStarted) {
        timeRemaining.value = duration
      }
    }

    nback.streak.launchCollectLatest(viewModelScope) { streak ->
      if (streak > 0 && (streak % 5) == 0) {
        val speedIncrease = (gameSpeed.value - fastestTickRate) / 10
        gameSpeed.value -= speedIncrease
      }
    }
  }

  fun startGameKey() {
    when (gameState.value) {
      GameState.Running -> {}
      GameState.Paused -> resumeGame()
      GameState.NotStarted, GameState.GameOver -> startGame()
    }
  }

  fun startGame() {
    if (_gameState.value != GameState.Running) {
      quitGame()
      _gameState.value = GameState.Running
      fillNextPieces()
      spawnNewPiece()
      startGameLoop()
      startTimer()
      nback.onGameStarted()
    }
  }

  fun pauseBindingInvoked() {
    if (_gameState.value == GameState.Running) {
      _gameState.value = GameState.Paused
      gameJob?.cancel()
      timerJob?.cancel()
    }
  }

  fun resumeGame() {
    if (_gameState.value == GameState.Paused) {
      _gameState.value = GameState.Running
      startGameLoop()
      startTimer()
    }
  }

  fun quitGame() {
    gameJob?.cancel()
    timerJob?.cancel()
    board.value = Board()
    currentTetrimino.value = null
    nextTetriminos.value = emptyList()
    tetriminoHistory.clear()
    nback.reset()
    comboStreak.value = 0
    score.value = 0
    gameSpeed.value = initialGameTickRate
    timeRemaining.value = gameSettings.gameDuration.value
    _gameState.value = GameState.NotStarted
  }

  private fun startGameLoop() {
    gameJob?.cancel()
    gameJob = gameScope.launch {
      while (gameState.value == GameState.Running) {
        delay(gameSpeed.value)
        tick()
      }
    }
  }

  private fun tick() {
    if (_gameState.value != GameState.Running) return

    if (!moveTetriminoDown()) {
      lockTetrimino()
      if (!spawnNewPiece()) gameOver(false)
    }
  }

  private fun gameOver(timeElapsed: Boolean) {
    val currentScore = score.value
    val nBack = gameSettings.nbackSetting.value
    val gameDuration = gameSettings.gameDuration.value

    if (currentScore > currentHighScore.value) gameStatsData.putHighScore(currentScore, gameDuration, nBack)
    gameSettings.gameDuration.value = gameDuration.toInt() // TODO: Find a better way to update the current high score.
    val nextLevelUnlocked = timeElapsed && nback.checkLevelProgression()
    achievements.checkAchievements(nextLevelUnlocked, nback.setting.value)

    _gameState.value = GameState.GameOver
    gameJob?.cancel()
  }
  fun leftClicked() {
    if (_gameState.value != GameState.Running) return
    val position = currentPiecePosition.value ?: return

    val newPosition = position.copy(col = position.col - 1)
    if (board.value.validPosition(currentTetrimino.value?.tetrimino, newPosition)) {
      currentPiecePosition.value = newPosition
    }
  }

  fun rightClicked() {
    if (_gameState.value != GameState.Running) return
    val position = currentPiecePosition.value ?: return

    val newPosition = position.copy(col = position.col + 1)
    if (board.value.validPosition(currentTetrimino.value?.tetrimino, newPosition)) {
      currentPiecePosition.value = newPosition
    }
  }

  fun rotatePiece(direction: Rotation) {
    if (_gameState.value != GameState.Running) return
    val current = currentTetrimino.value ?: return
    val position = currentPiecePosition.value ?: return

    val rotatedPiece = current.tetrimino.rotate(direction)
    if (board.value.validPosition(rotatedPiece, position)) {
      currentTetrimino.value = TetriminoHistory.Entry(rotatedPiece, current.colorType)
    } else {
      // Try wall kick (adjust position if rotation would cause collision)
      for (offset in listOf(-1, 1, -2, 2)) {
        val newPosition = position.copy(col = position.col + offset)
        if (board.value.validPosition(rotatedPiece, newPosition)) {
          currentTetrimino.value = TetriminoHistory.Entry(rotatedPiece, current.colorType)
          currentPiecePosition.value = newPosition
          break
        }
      }
    }
  }

  fun dropPiece() {
    if (_gameState.value != GameState.Running) return

    while (moveTetriminoDown());
    lockTetrimino()
    if (!spawnNewPiece()) gameOver(false)
  }

  fun downClicked(): Boolean {
    if (_gameState.value != GameState.Running) return false
    return moveTetriminoDown()
  }

  private fun moveTetriminoDown(): Boolean {
    val position = currentPiecePosition.value ?: return false

    val newPosition = position.copy(row = position.row + 1)
    val valid = board.value.validPosition(currentTetrimino.value?.tetrimino, newPosition)
    if (valid) currentPiecePosition.value = newPosition

    return valid
  }

  fun nbackMatchChoice(type: NbackStimulus.Type) = nback.matchChoice(tetriminoHistory.entries, type)
  fun nbackNoMatchChoice(type: NbackStimulus.Type) = nback.noMatchChoice(tetriminoHistory.entries, type)

  private fun lockTetrimino() {
    val current = currentTetrimino.value ?: return
    val position = currentPiecePosition.value ?: return

    gameSettings.nbackSetting.value.forEach {
      if (nback.currentTetriminoMatchChoicesEntered[it.type] == false) nbackNoMatchChoice(it.type)
    }

    val boardUpdate = mutableMapOf<Int, Map<Int, Int>>()

    current.tetrimino.shape.forEachIndexed { row, columns ->
      val rowUpdate = mutableMapOf<Int, Int>()
      boardUpdate[row + position.row] = rowUpdate

      columns.forEachIndexed { column, tetriminoType ->
        if (tetriminoType != 0) rowUpdate[column + position.col] = Tetrimino.lockedType
      }
    }

    nback.clearMatchChoices()
    board.value = board.value.copy(boardUpdate)
    val completedLines = clearFilledRows()

    if (completedLines > 0) comboStreak.value++
    else comboStreak.value = 0

    addScore(completedLines, comboStreak.value)
  }

  private fun clearFilledRows(): Int {
    var completedLines = 0
    val newMatrix = board.value.matrix.deepCopy()

    // Iterate from the bottom of the well to the top.
    var row = boardHeight - 1
    while (row >= 0) {
      val columns = newMatrix[row]

      if (columns.all { it != 0 }) {
        // Remove the line and shift everything down.
        for (r in row downTo 1) {
          newMatrix[r] = newMatrix[r - 1].copyOf()
        }
        newMatrix[0] = IntArray(boardWidth) { 0 }
        completedLines++
      } else {
        row--
      }
    }

    if (completedLines > 0) {
      board.value = Board(newMatrix)
    }

    return completedLines
  }

  private fun addScore(completedLines: Int, comboStreak: Int) {
    val baseScore = when (completedLines) {
      0 -> 20
      1 -> 100
      2 -> 300
      3 -> 500
      4 -> 1000
      else -> 0
    }
    val comboMultiplier = when(comboStreak) {
      1 -> 1f
      2 -> 1.25f
      3 -> 2f
      else -> {
        if (comboStreak > 3) comboStreak.toFloat()
        else 1f
      }
    }
    score.value += (baseScore * comboMultiplier * nback.multiplier.value).toLong()
  }

  private fun spawnNewPiece(): Boolean {
    currentTetrimino.value = nextTetriminos.value.firstOrNull() ?: throw IllegalStateException()
    nextTetriminos.value = nextTetriminos.value.subList(1, nextTetriminos.value.size)
    if (morePiecesNeeded()) fillNextPieces()
    currentTetrimino.value?.let { tetriminoHistory.add(it.tetrimino, it.colorType) }
    currentPiecePosition.value = newTetriminoStartPosition

    return board.value.validPosition(currentTetrimino.value?.tetrimino, newTetriminoStartPosition)
  }

  private fun fillNextPieces() {
    val unpickedNextPieces = Tetrimino.types.toMutableList()
    val nextPieces = nextTetriminos.value.toMutableList()

    while (unpickedNextPieces.isNotEmpty()) {
      val nbackLevel = nback.level.value

      val lastMatchDistance = lastMatchDistance(nextPieces)
      val matchChance = when (lastMatchDistance) {
        null -> {
          min(.95f, nbackBaseMatchChance + (sqrt((nextPieces.size + tetriminoHistory.entries.size).toFloat()) * nbackMatchChanceGrowthFactor))
        }
        0 -> {
          val streakLength = matchStreakLength(nextPieces)
          max(0.05f, nbackBaseMatchChance - sqrt(streakLength.toFloat()) * nbackMatchChanceDecayFactor)
        }
        else -> min(.95f, nbackBaseMatchChance + (sqrt(lastMatchDistance.toFloat()) * nbackMatchChanceGrowthFactor))
      }

      val nextPiece = if (nbackLevel < nextPieces.size && Random.nextFloat() < matchChance) {
        nextPieces.getOrNull(nextPieces.size - nbackLevel) ?: throw IllegalStateException()
      } else {
        val nextTetrimino = unpickedNextPieces.random()
        val nextColor = if (nback.colorNbackEnabled.value) NbackTetriminoColor.random()
          else NbackTetriminoColor.fromIndex(nextTetrimino.type)
        TetriminoHistory.Entry(nextTetrimino, nextColor)
      }

      unpickedNextPieces.remove(nextPiece.tetrimino)
      nextPieces.add(nextPiece)
    }
    nextTetriminos.value = nextPieces
    if (morePiecesNeeded()) fillNextPieces()
  }

  private fun lastMatchDistance(nextPieces: List<TetriminoHistory.Entry>): Int? {
    val reversed = nextPieces.reversed() + tetriminoHistory.entries.reversed()

    reversed.forEachIndexed { index, tetrimino ->
      val matchPiece = reversed.getOrNull(index + nback.level.value) ?: return null
      if (matchPiece.tetrimino.type == tetrimino.tetrimino.type) return index
    }
    return null
  }
  private fun matchStreakLength(nextPieces: List<TetriminoHistory.Entry>): Int {
    val reversed = nextPieces.reversed() + tetriminoHistory.entries.reversed()
    var streak = 0
    reversed.forEachIndexed { index, tetrimino ->
      val matchPiece = reversed.getOrNull(index + nback.level.value) ?: return streak
      if (matchPiece.tetrimino.type == tetrimino.tetrimino.type) streak++
      else return streak
    }
    return streak
  }

  private fun morePiecesNeeded() = nextTetriminos.value.size < (14 + nback.level.value)

  private fun startTimer() {
    timerJob?.cancel()
    timerJob = gameScope.launch {
      val startTime = Clock.System.now().toEpochMilliseconds()
      while (timeRemaining.value > 0 && _gameState.value == GameState.Running) {
        val drift = (Clock.System.now().toEpochMilliseconds() - startTime) % 1000L
        delay(1000L - drift)
        timeRemaining.value--
      }

      if (timeRemaining.value <= 0 && _gameState.value == GameState.Running) {
        gameOver(true)
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    gameJob?.cancel()
    timerJob?.cancel()
  }
}

enum class GameState {
  NotStarted,
  Running,
  Paused,
  GameOver
}