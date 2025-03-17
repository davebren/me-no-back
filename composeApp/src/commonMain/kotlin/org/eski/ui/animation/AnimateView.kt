package org.eski.ui.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AnimateView(
  modifier: Modifier = Modifier,
  visible: Boolean,
  enter: EnterTransition = fadeIn(),
  exit: ExitTransition = fadeOut(),
  content: @Composable () -> Unit
) {
  AnimatedVisibility(
    modifier = modifier,
    visible = visible,
    enter = enter,
    exit = exit
  ) {
    content()
  }
}

@Composable
fun ColumnScope.AnimateView(
  modifier: Modifier = Modifier,
  visible: Boolean,
  enter: EnterTransition = fadeIn(),
  exit: ExitTransition = fadeOut(),
  content: @Composable () -> Unit
) {
  AnimatedVisibility(
    modifier = modifier,
    visible = visible,
    enter = enter,
    exit = exit
  ) {
    content()
  }
}