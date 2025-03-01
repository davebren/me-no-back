package org.eski.menoback.ui.keybinding

import androidx.compose.ui.input.key.Key
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeyBindingSettings(val settings: Settings) {
  companion object {
    private const val settingsKey = "settings.keybinding"

    private const val moveLeftKey = "$settingsKey.moveLeft"
    private const val moveRightKey = "$settingsKey.moveRight"
    private const val moveDownKey = "$settingsKey.moveDown"
    private const val rotateClockwiseKey = "$settingsKey.rotateClockwise"
    private const val rotateCounterClockwiseKey = "$settingsKey.rotateCounterClockwise"
    private const val rotate180Key = "$settingsKey.rotate180"
    private const val dropPieceKey = "$settingsKey.dropPiece"
    private const val nbackMatchKey = "$settingsKey.nbackMatch"
    private const val togglePlayPauseKey = "$settingsKey.togglePlayPause"

    // Default keybindings
    private val defaultMoveLeft = Key.DirectionLeft.keyCode
    private val defaultMoveRight = Key.DirectionRight.keyCode
    private val defaultMoveDown = Key.DirectionDown.keyCode
    private val defaultRotateClockwise = Key.V.keyCode
    private val defaultRotateCounterClockwise = Key.Q.keyCode
    private val defaultRotate180 = Key.DirectionUp.keyCode
    private val defaultDropPiece = Key.Spacebar.keyCode
    private val defaultNbackMatch = Key.Z.keyCode
    private val defaultTogglePlayPause = Key.Enter.keyCode
  }

  // StateFlows for each keybinding
  private val _moveLeft = MutableStateFlow(settings.getLong(moveLeftKey, defaultMoveLeft))
  val moveLeft = _moveLeft.asStateFlow()

  private val _moveRight = MutableStateFlow(settings.getLong(moveRightKey, defaultMoveRight))
  val moveRight = _moveRight.asStateFlow()

  private val _moveDown = MutableStateFlow(settings.getLong(moveDownKey, defaultMoveDown))
  val moveDown = _moveDown.asStateFlow()

  private val _rotateClockwise = MutableStateFlow(settings.getLong(rotateClockwiseKey, defaultRotateClockwise))
  val rotateClockwise = _rotateClockwise.asStateFlow()

  private val _rotateCounterClockwise = MutableStateFlow(settings.getLong(rotateCounterClockwiseKey, defaultRotateCounterClockwise))
  val rotateCounterClockwise = _rotateCounterClockwise.asStateFlow()

  private val _rotate180 = MutableStateFlow(settings.getLong(rotate180Key, defaultRotate180))
  val rotate180 = _rotate180.asStateFlow()

  private val _dropPiece = MutableStateFlow(settings.getLong(dropPieceKey, defaultDropPiece))
  val dropPiece = _dropPiece.asStateFlow()

  private val _nbackMatch = MutableStateFlow(settings.getLong(nbackMatchKey, defaultNbackMatch))
  val nbackMatch = _nbackMatch.asStateFlow()

  private val _togglePlayPause = MutableStateFlow(settings.getLong(togglePlayPauseKey, defaultTogglePlayPause))
  val togglePlayPause = _togglePlayPause.asStateFlow()

  // Functions to update keybindings
  fun setMoveLeft(keyCode: Long) {
    _moveLeft.value = keyCode
    settings.putLong(moveLeftKey, keyCode)
  }

  fun setMoveRight(keyCode: Long) {
    _moveRight.value = keyCode
    settings.putLong(moveRightKey, keyCode)
  }

  fun setMoveDown(keyCode: Long) {
    _moveDown.value = keyCode
    settings.putLong(moveDownKey, keyCode)
  }

  fun setRotateClockwise(keyCode: Long) {
    _rotateClockwise.value = keyCode
    settings.putLong(rotateClockwiseKey, keyCode)
  }

  fun setRotateCounterClockwise(keyCode: Long) {
    _rotateCounterClockwise.value = keyCode
    settings.putLong(rotateCounterClockwiseKey, keyCode)
  }

  fun setRotate180(keyCode: Long) {
    _rotate180.value = keyCode
    settings.putLong(rotate180Key, keyCode)
  }

  fun setDropPiece(keyCode: Long) {
    _dropPiece.value = keyCode
    settings.putLong(dropPieceKey, keyCode)
  }

  fun setNbackMatch(keyCode: Long) {
    _nbackMatch.value = keyCode
    settings.putLong(nbackMatchKey, keyCode)
  }

  fun setTogglePlayPause(keyCode: Long) {
    _togglePlayPause.value = keyCode
    settings.putLong(togglePlayPauseKey, keyCode)
  }

  fun resetToDefaults() {
    setMoveLeft(defaultMoveLeft)
    setMoveRight(defaultMoveRight)
    setMoveDown(defaultMoveDown)
    setRotateClockwise(defaultRotateClockwise)
    setRotateCounterClockwise(defaultRotateCounterClockwise)
    setRotate180(defaultRotate180)
    setDropPiece(defaultDropPiece)
    setNbackMatch(defaultNbackMatch)
    setTogglePlayPause(defaultTogglePlayPause)
  }
}