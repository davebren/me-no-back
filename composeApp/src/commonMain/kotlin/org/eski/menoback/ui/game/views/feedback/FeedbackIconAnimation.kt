package org.eski.menoback.ui.game.views.feedback

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import org.eski.menoback.ui.game.model.FeedbackMode
import org.eski.menoback.ui.game.vm.GameNbackViewModel.*
import org.eski.menoback.ui.game.vm.GameScreenViewModel

private const val feedbackDurationMillis = 500

@Composable fun BoxScope.FeedbackIconAnimation(vm: GameScreenViewModel) {
  val feedbackState by vm.nback.feedback.collectAsState()
  val feedbackMode by vm.feedbackMode.collectAsState()

  if (!feedbackMode.contains(FeedbackMode.icon) || feedbackState == FeedbackState.none) return

  var animationTriggered by remember { mutableStateOf(false) }
  val animationAlpha by animateFloatAsState(
    targetValue = if (animationTriggered) 0.3f else 0.1f,
    animationSpec = tween(durationMillis = feedbackDurationMillis / 2),
    label = "feedbackAlpha"
  )

  LaunchedEffect(feedbackState) {
    animationTriggered = true
    delay((feedbackDurationMillis / 2).toLong())
    animationTriggered = false
  }
  if (feedbackState == FeedbackState.correct) {
    Icon(
      modifier = Modifier.align(alignment = Alignment.Center).fillMaxSize(0.4f),
      imageVector = Icons.Filled.Check,
      contentDescription = "Match Correct",
      tint = Color.Green.copy(alpha = animationAlpha),
    )
  } else if (feedbackState == FeedbackState.incorrect) {
    Icon(
      modifier = Modifier.align(alignment = Alignment.Center).fillMaxSize(0.4f),
      imageVector = Icons.Filled.Clear,
      contentDescription = "Match Incorrect",
      tint = Color.Red.copy(alpha = animationAlpha),
    )
  }
}