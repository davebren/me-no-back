package org.eski.menoback.ui.keybinding

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import kotlinx.coroutines.delay
import org.eski.menoback.ui.game.model.NbackStimulus
import org.eski.menoback.ui.game.model.Rotation
import org.eski.menoback.ui.game.vm.GameScreenViewModel

const val repeatDelayMillis = 250L
const val repeatTickMillis = 50L

@Composable
fun KeyboardInput(
  vm: GameScreenViewModel,
  keyBindingSettings: KeyBindingSettings
) {
  val focusRequester = remember { FocusRequester() }
  var hasFocus by remember { mutableStateOf(false) }
  var leftPressed by remember { mutableStateOf(false) }
  var rightPressed by remember { mutableStateOf(false) }
  var downPressed by remember { mutableStateOf(false) }

  val moveLeft by keyBindingSettings.moveLeft.collectAsState()
  val moveRight by keyBindingSettings.moveRight.collectAsState()
  val moveDown by keyBindingSettings.moveDown.collectAsState()
  val rotateClockwise by keyBindingSettings.rotateClockwise.collectAsState()
  val rotateCounterClockwise by keyBindingSettings.rotateCounterClockwise.collectAsState()
  val rotate180 by keyBindingSettings.rotate180.collectAsState()
  val dropPiece by keyBindingSettings.dropPiece.collectAsState()
  val nbackMatch by keyBindingSettings.nbackMatch.collectAsState()
  val startGame by keyBindingSettings.startGame.collectAsState()
  val pauseGame by keyBindingSettings.pauseGame.collectAsState()

  Box(modifier = Modifier
    .focusRequester(focusRequester)
    .onFocusChanged { hasFocus = it.isFocused }
    .focusable()
    .onKeyEvent { event ->
      if (event.isCtrlPressed || event.isShiftPressed || event.isMetaPressed || event.isAltPressed) {
        return@onKeyEvent false
      }

      val keyCode = event.key.keyCode

      if (event.type == KeyEventType.KeyDown) {
        when (keyCode) {
          startGame -> vm.startGameKey()
          pauseGame -> vm.pauseBindingInvoked()

          moveLeft -> {
            if (!leftPressed) {
              vm.leftClicked()
              leftPressed = true
            }
          }
          moveRight -> {
            if (!rightPressed) {
              vm.rightClicked()
              rightPressed = true
            }
          }
          moveDown -> {
            if (!downPressed) {
              vm.downClicked()
              downPressed = true
            }
          }

          rotateClockwise -> vm.rotatePiece(Rotation.clockwise)
          rotateCounterClockwise -> vm.rotatePiece(Rotation.counterClockwise)
          rotate180 -> {
            vm.rotatePiece(Rotation.clockwise)
            vm.rotatePiece(Rotation.clockwise)
          }

          dropPiece -> vm.dropPiece()
          nbackMatch -> vm.nbackMatchChoice(NbackStimulus.Type.block)
        }
      }

      if (event.type == KeyEventType.KeyUp) {
        when(keyCode) {
          moveLeft -> leftPressed = false
          moveRight -> rightPressed = false
          moveDown -> downPressed = false
        }
      }

      return@onKeyEvent false
    }
  )

  LaunchedEffect(leftPressed) {
    delay(repeatDelayMillis)
    while (leftPressed) {
      vm.leftClicked()
      delay(repeatTickMillis)
    }
  }

  LaunchedEffect(rightPressed) {
    delay(repeatDelayMillis)
    while (rightPressed) {
      vm.rightClicked()
      delay(repeatTickMillis)
    }
  }

  LaunchedEffect(downPressed) {
    delay(repeatDelayMillis)
    while (downPressed) {
      val valid = vm.downClicked()
      if (valid) {
        delay(repeatTickMillis)
      } else {
        downPressed = false
        delay(repeatTickMillis)
      }
    }
  }

  if (!hasFocus) {
    LaunchedEffect(Unit) {
      focusRequester.requestFocus()
    }
  }
}