package org.eski.menoback.ui.game.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import kotlinx.coroutines.delay
import org.eski.menoback.ui.game.vm.GameNbackViewModel.FeedbackState
import org.eski.menoback.ui.game.vm.GameScreenViewModel

const val feedbackDurationMillis = 300

@Composable
fun Modifier.feedback(vm: GameScreenViewModel): Modifier = composed {
  val feedbackState by vm.nback.feedback.collectAsState()
  if (feedbackState == FeedbackState.none) return@composed this@composed

  var animationTriggered by remember { mutableStateOf(false) }
  val animationAlpha by animateFloatAsState(
    targetValue = if (animationTriggered) 0.3f else 0.1f,
    animationSpec = tween(durationMillis = feedbackDurationMillis / 2),
    label = "feedbackAlpha"
  )

  val feedbackColor = when (feedbackState) {
    FeedbackState.correct -> vm.appColors.value.feedbackCorrect
    FeedbackState.incorrect -> vm.appColors.value.feedbackIncorrect
    FeedbackState.none -> throw IllegalStateException()
  }

  LaunchedEffect(feedbackState) {
    animationTriggered = true
    delay((feedbackDurationMillis / 2).toLong())
    animationTriggered = false
  }

  drawBehind {
    drawRect(
      color = feedbackColor.copy(alpha = animationAlpha),
      size = size
    )
  }
}