package org.eski.menoback.ui.game.views

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.animations.AnimateView
import org.eski.animations.AnimatedCountdownTimer
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.menoback.ui.game.vm.GameState
import org.eski.ui.views.PressExpandButton
import org.eski.ui.views.PressExpandButtonState
import org.eski.ui.views.PressExpandButtonState.Companion.transitionSpecDp
import org.eski.ui.util.grid2
import org.eski.ui.util.grid6
import org.eski.ui.util.grid8
import org.eski.ui.util.square
import org.eski.ui.views.CenteredVerticalText
import org.eski.ui.views.FloatingButton
import org.eski.ui.views.PressExpandButtonState.expanded
import org.eski.ui.views.PressExpandButtonState.pressed


@Composable
fun GameStartButton(
  vm: GameScreenViewModel,
  visible: Boolean,
  containerSize: IntSize
) {
  val containerSizeDp = with(LocalDensity.current) { DpSize(containerSize.width.toDp(), containerSize.height.toDp()) }
  val expandedFinished = remember { mutableStateOf(false) }
  val gameState by vm.gameState.collectAsState()

  AnimateView(
    visible = gameState == GameState.Paused || gameState == GameState.NotStarted || gameState == GameState.GameOver,
  ) {
    Column(horizontalAlignment = Alignment.End) {
      Spacer(modifier = Modifier.fillMaxSize().weight(1f))

      AnimateView(
        visible = gameState == GameState.Paused || gameState == GameState.NotStarted || gameState == GameState.GameOver,
        enter = slideInVertically(animationSpec = tween(300, 0)) { height -> height },
        exit = slideOutVertically(animationSpec = tween(300, 0)) { height -> height }
      ) {
        val unpressedSize = FloatingButton.large.unpressedSize.output(containerSizeDp.height).square()
        val pressedSize = FloatingButton.large.pressedSize.output(containerSizeDp.height).square()
        val verticalPadding = FloatingButton.large.bottomMargin.output(containerSizeDp.height)
        val expandable = gameState != GameState.Paused

        PressExpandButton(
          unexpandableClickable = {  if (gameState == GameState.Paused) vm.resumeGame() },
          expandedSize = containerSize,
          expandable = expandable,
          offset = DpOffset(0.dp, 0.dp),
          size = PressExpandButtonState.Map(
            unpressed = unpressedSize,
            pressed = pressedSize,
            expanded = containerSizeDp
          ),
          elevation = PressExpandButtonState.Map(unpressed = 16.dp, 32.dp, 64.dp),
          horizontalPadding = grid2,
          verticalPadding = verticalPadding,
          cornerRadius = PressExpandButtonState.Map(unpressed = 40.dp, pressed = 44.dp, expanded = 0.dp),
          backgroundColor = PressExpandButtonState.staticMap(Color.Green),
          border = BorderStroke(4.dp, Color.DarkGray),
          clickable = vm.startButtonClickable,
        ) { transition ->
          expandedFinished.value = transition.currentState == expanded && transition.targetState == expanded

          if (transition.targetState != expanded) {
            val iconSize by transition.animateDp(transitionSpecDp) { targetState ->
              when(targetState) {
                PressExpandButtonState.unpressed -> grid6
                pressed -> grid8
                expanded -> 0.dp
              }
            }

            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.size(grid6),
            ) {
              Icon(
                imageVector = if (gameState == GameState.GameOver) Icons.Filled.Refresh else Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(iconSize)
              )
            }
          }

          if (expandedFinished.value) {
            Countdown { vm.startGame() }
          }
        }
      }
    }
  }
}

@Composable
private fun Countdown(
  onFinished: () -> Unit
) {
  var timer by remember { mutableStateOf(3) }
  val coroutineScope = rememberCoroutineScope()
  val animatedCountdownTimer = remember { AnimatedCountdownTimer(coroutineScope) }

  CenteredVerticalText(
    modifier = Modifier.graphicsLayer {
      scaleX = animatedCountdownTimer.scale
      scaleY = animatedCountdownTimer.scale
      alpha = animatedCountdownTimer.alpha
    },
    text = if (timer == 0) "Go!" else "$timer",
    fontSize = 30.sp,
    color = Color.White
  )

  LaunchedEffect(Unit) {
    animatedCountdownTimer.start(3, 0) {
      timer = it
      if (timer == 0) onFinished.invoke()
    }
  }
}