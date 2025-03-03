package org.eski.menoback.ui.game.vm

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import org.eski.menoback.ui.game.model.Rotation
import org.eski.menoback.ui.game.data.GameSettings
import org.eski.menoback.ui.game.data.GameStatsData
import org.eski.menoback.data.gameStatsData as defaultGameStatsData
import org.eski.util.deepCopy
import kotlin.random.Random

const val nbackMatchChance = 0.2f
const val initialGameTickRate = 1000L

class GameScreenViewModel(
  private val gameSettings: GameSettings,
  private val gameStatsData: GameStatsData = defaultGameStatsData
) : ViewModel() {
  val appColors = MutableStateFlow(AppColors())
  val tetriminoColors = MutableStateFlow(TetriminoColors())

  private val _gameState = MutableStateFlow<GameState>(GameState.NotStarted)
  val gameState: StateFlow<GameState> = _gameState.asStateFlow()

  val currentTetrimino = MutableStateFlow<Tetrimino?>(null)
  val currentPiecePosition = MutableStateFlow<Tetrimino.Position?>(null)
  val nextTetriminos = MutableStateFlow<List<Tetrimino>>(emptyList())
  val nextTetrimino = nextTetriminos.map {
    it.firstOrNull()
  }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
  private val tetriminoHistory = mutableListOf<Tetrimino>()

  val board = MutableStateFlow(Board())
  val displayBoard: StateFlow<Board> = combine(board, currentTetrimino, currentPiecePosition) {
      board: Board, tetrimino: Tetrimino?, position: Tetrimino.Position? ->
    if (tetrimino == null || position == null) board
    else board.with(tetrimino, position)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Board())

  val nback = GameNbackViewModel(viewModelScope, _gameState, currentTetrimino)
  val score = MutableStateFlow<Int>(0)
  private val gameSpeed = MutableStateFlow(initialGameTickRate)

  private var gameJob: Job? = null
  private val gameScope = CoroutineScope(Dispatchers.Default)

  val timeRemaining = MutableStateFlow(gameSettings.gameDuration.value)
  val timerColor = timeRemaining.map {
    val warningThreshold = gameSettings.gameDuration.value / 6
    if (it < warningThreshold) Color.Red else Color.LightGray
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Color.LightGray)

  val currentHighScore: StateFlow<StateFlow<Int>?> = gameSettings.gameDuration.map { duration ->
    gameStatsData.highScores[duration]
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
  val currentHighScoreText = MutableStateFlow(gameStatsData.formatHighScoreText(gameSettings.gameDuration.value))


  private var timerJob: Job? = null

  // Initialize the time remaining when game settings change
  init {
    viewModelScope.launch {
      gameSettings.gameDuration.collect { duration ->
        if (_gameState.value == GameState.NotStarted) {
          timeRemaining.value = duration
          currentHighScoreText.value = gameStatsData.formatHighScoreText(duration)
        }
      }
    }
  }

  fun toggleGameState() {
    when (gameState.value) {
      GameState.NotStarted -> startGame()
      GameState.Running -> pauseGame()
      GameState.Paused -> resumeGame()
      GameState.GameOver -> startGame()
    }
  }

  fun startGame() {
    if (_gameState.value != GameState.Running) {
      resetGame()
      _gameState.value = GameState.Running
      fillNextPieces()
      spawnNewPiece()
      startGameLoop()
      startTimer()
    }
  }

  fun pauseGame() {
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

  fun resetGame() {
    gameJob?.cancel()
    timerJob?.cancel()
    board.value = Board()
    currentTetrimino.value = null
    nextTetriminos.value = emptyList()
    tetriminoHistory.clear()
    nback.streak.value = 0
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

      if (!spawnNewPiece()) {
        gameOver()
      }
    }
  }

  private fun gameOver() {
    // Update high score if needed
    val currentScore = score.value
    val nBackLevel = nback.level.value
    val gameDuration = gameSettings.gameDuration.value

    gameStatsData.updateHighScore(gameDuration, currentScore, nBackLevel)
    currentHighScoreText.value = gameStatsData.formatHighScoreText(gameDuration)

    // Set game state to game over
    _gameState.value = GameState.GameOver
    gameJob?.cancel()
  }

  fun leftClicked() {
    if (_gameState.value != GameState.Running) return
    val position = currentPiecePosition.value ?: return

    val newPosition = position.copy(col = position.col - 1)
    if (board.value.validPosition(currentTetrimino.value, newPosition)) {
      currentPiecePosition.value = newPosition
    }
  }

  fun rightClicked() {
    if (_gameState.value != GameState.Running) return
    val position = currentPiecePosition.value ?: return

    val newPosition = position.copy(col = position.col + 1)
    if (board.value.validPosition(currentTetrimino.value, newPosition)) {
      currentPiecePosition.value = newPosition
    }
  }

  fun rotatePiece(direction: Rotation) {
    if (_gameState.value != GameState.Running) return
    val tetrimino = currentTetrimino.value ?: return
    val position = currentPiecePosition.value ?: return

    val rotatedPiece = tetrimino.rotate(direction)
    if (board.value.validPosition(rotatedPiece, position)) {
      currentTetrimino.value = rotatedPiece
    } else {
      // Try wall kick (adjust position if rotation would cause collision)
      for (offset in listOf(-1, 1, -2, 2)) {
        val newPosition = position.copy(col = position.col + offset)
        if (board.value.validPosition(rotatedPiece, newPosition)) {
          currentTetrimino.value = rotatedPiece
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
    spawnNewPiece()
  }

  fun downClicked(): Boolean {
    if (_gameState.value != GameState.Running) return false
    return moveTetriminoDown()
  }

  private fun moveTetriminoDown(): Boolean {
    val position = currentPiecePosition.value ?: return false

    val newPosition = position.copy(row = position.row + 1)
    val valid = board.value.validPosition(currentTetrimino.value, newPosition)
    if (valid) currentPiecePosition.value = newPosition

    return valid
  }

  fun nbackMatchChoice() = nback.matchChoice(tetriminoHistory)
  fun nbackNoMatchChoice() = nback.noMatchChoice(tetriminoHistory)

  private fun lockTetrimino() {
    val tetrimino = currentTetrimino.value ?: return
    val position = currentPiecePosition.value ?: return

    if (!nback.matchChoiceMade) nbackNoMatchChoice()

    val boardUpdate = mutableMapOf<Int, Map<Int, Int>>()

    tetrimino.shape.forEachIndexed { row, columns ->
      val rowUpdate = mutableMapOf<Int, Int>()
      boardUpdate[row + position.row] = rowUpdate

      columns.forEachIndexed { column, tetriminoType ->
        if (tetriminoType != 0) rowUpdate[column + position.col] = Tetrimino.lockedType
      }
    }

    nback.clearMatchChoice()
    board.value = board.value.copy(boardUpdate)
    val completedLines = clearFilledRows()
    addScore(completedLines)
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

  private fun addScore(completedLines: Int) {
    val baseScore = when (completedLines) {
      0 -> 20
      1 -> 100
      2 -> 300
      3 -> 500
      4 -> 1000
      else -> 0
    }
    score.value += (baseScore * nback.multiplier.value).toInt()
  }

  private fun spawnNewPiece(): Boolean {
    currentTetrimino.value = nextTetriminos.value.firstOrNull() ?: throw IllegalStateException()
    nextTetriminos.value = nextTetriminos.value.subList(1, nextTetriminos.value.size)
    if (morePiecesNeeded()) fillNextPieces()
    currentTetrimino.value?.let { tetriminoHistory.add(it) }
    currentPiecePosition.value = newTetriminoStartPosition

    return board.value.validPosition(currentTetrimino.value, newTetriminoStartPosition)
  }

  private fun fillNextPieces() {
    val unpickedNextPieces = Tetrimino.types.toMutableList()
    val nextPieces = nextTetriminos.value.toMutableList()

    while (unpickedNextPieces.isNotEmpty()) {
      val nbackLevel = nback.level.value

      val nextPiece = if (nbackLevel < nextPieces.size && Random.nextFloat() < nbackMatchChance) {
        nextPieces.getOrNull(nextPieces.size - nbackLevel) ?: throw IllegalStateException()
      } else unpickedNextPieces.random()

      unpickedNextPieces.remove(nextPiece)
      nextPieces.add(nextPiece)
    }
    nextTetriminos.value = nextPieces
    if (morePiecesNeeded()) fillNextPieces()
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
        gameOver()
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