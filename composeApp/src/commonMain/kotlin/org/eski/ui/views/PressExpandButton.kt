package org.eski.ui.views

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.eski.ui.util.topWindowInset
import org.eski.ui.views.PressExpandButtonState.Companion.cornerRadiusMap
import org.eski.ui.views.PressExpandButtonState.Companion.elevationMap
import org.eski.ui.views.PressExpandButtonState.Companion.textSizeMap
import org.eski.ui.views.PressExpandButtonState.Companion.transitionSpecColor
import org.eski.ui.views.PressExpandButtonState.Companion.transitionSpecDp
import org.eski.ui.views.PressExpandButtonState.Companion.transitionSpecInt
import org.eski.ui.views.PressExpandButtonState.expanded
import org.eski.ui.views.PressExpandButtonState.pressed
import org.eski.ui.views.PressExpandButtonState.unpressed


enum class PressExpandButtonState {
  unpressed, pressed, expanded;

  class Map<T>(val unpressed: T, val pressed: T = unpressed, val expanded: T = unpressed) {
    fun stateValue(state: PressExpandButtonState): T = when(state) {
      PressExpandButtonState.unpressed -> unpressed
      PressExpandButtonState.pressed -> pressed
      PressExpandButtonState.expanded -> expanded
    }
  }

  companion object {
    internal val elevationMap = Map(2.dp, 8.dp, 8.dp)
    internal val textSizeMap = Map(24, 26, 28)
    internal val paddingMap = Map(16.dp, 12.dp, 0.dp)
    internal val cornerRadiusMap = Map(2.dp, 2.dp, 0.dp)

    private const val shrinkTransitionDuration = 600
    private const val pressTransitionDuration = 200
    private const val expandTransitionDuration = 350

    private fun <T> transitionSpec(): @Composable Transition.Segment<PressExpandButtonState>.() -> FiniteAnimationSpec<T> =
      {
        when (this.targetState) {
          unpressed -> tween(durationMillis = shrinkTransitionDuration, 0)
          pressed -> tween(durationMillis = pressTransitionDuration, 0)
          expanded -> tween(durationMillis = expandTransitionDuration, 0)
        }
      }

    val transitionSpecDp = transitionSpec<Dp>()
    val transitionSpecColor = transitionSpec<Color>()
    val transitionSpecInt = transitionSpec<Int>()
    val transitionSpecFloat = transitionSpec<Float>()

    fun <T> staticMap(value: T) = Map(value, value, value)
  }
}

@Composable
fun PressExpandButton(
  modifier: Modifier = Modifier,
  unexpandableClickable: (() -> Unit)? = null,
  expandable: Boolean,
  offset: DpOffset,
  expandedSize: IntSize,
  text: String? = null,
  unexpandedZIndex: Float = 0f,
  expandedZIndex: Float = 1f,
  backgroundColor: PressExpandButtonState.Map<Color> = PressExpandButtonState.Map(pressed = Color.Gray, unpressed = Color.DarkGray),
  border: BorderStroke? = null,
  textColor: PressExpandButtonState.Map<Color> = PressExpandButtonState.Map(Color.White),
  textSize: PressExpandButtonState.Map<Int> = textSizeMap,
  elevation: PressExpandButtonState.Map<Dp> = elevationMap,
  horizontalPadding: Dp = 16.dp,
  verticalPadding: Dp = 16.dp,
  cornerRadius: PressExpandButtonState.Map<Dp> = cornerRadiusMap,
  expandedStateFlow: MutableStateFlow<Boolean>? = null,
  clickable: StateFlow<Boolean>? = null,
  size: PressExpandButtonState.Map<DpSize> = PressExpandButtonState.Map(
    unpressed = DpSize(
      width = with(LocalDensity.current) { expandedSize.width.toDp() } - (horizontalPadding * 2),
      height = 64.dp
    ),
    pressed = DpSize(
      width = (with(LocalDensity.current) { expandedSize.width.toDp() } + 8.dp) - (horizontalPadding * 2),
      height = 72.dp
    ),
    expanded = with(LocalDensity.current) { DpSize(expandedSize.width.toDp(), expandedSize.height.toDp()) }
  ),
  content: (@Composable (Transition<PressExpandButtonState>) -> Unit)? = null,
) {

  val clickableState = clickable?.collectAsState()
  val expandedState = expandedStateFlow?.collectAsState()

  val buttonState = remember { MutableTransitionState(unpressed) }
  val transition = rememberTransition(buttonState)

  if (expandedState?.value == false && buttonState.currentState == expanded) {
    buttonState.targetState = unpressed
  }

  val pressedHeightDiffHalf = ((size.stateValue(pressed).height - size.stateValue(unpressed).height) * .5f)
  val heightAnimated by transition.animateDp(transitionSpecDp) { targetState -> size.stateValue(targetState).height }
  val pressedWidthDiffHalf = ((size.stateValue(pressed).width - size.stateValue(unpressed).width) * .5f)
  val widthAnimated by transition.animateDp(transitionSpecDp) { targetState -> size.stateValue(targetState).width }

  val yOffset by transition.animateDp(transitionSpecDp) { targetState ->
    when(targetState) {
      unpressed -> offset.y + verticalPadding
      pressed -> (offset.y + verticalPadding) - pressedHeightDiffHalf
      expanded -> 0.dp
    }
  }

  val xOffset by transition.animateDp(transitionSpecDp) { targetState ->
    when(targetState) {
      unpressed -> offset.x + horizontalPadding
      pressed -> (offset.x + horizontalPadding) - pressedWidthDiffHalf
      expanded -> 0.dp
    }
  }

  val endPaddingAnimated by transition.animateDp(transitionSpecDp) { targetState ->
    when(targetState) {
      unpressed -> horizontalPadding
      pressed -> horizontalPadding - pressedWidthDiffHalf
      expanded -> 0.dp
    }
  }

  val bottomPaddingAnimated by transition.animateDp(transitionSpecDp) { targetState ->
    when(targetState) {
      unpressed -> verticalPadding
      pressed -> verticalPadding - pressedHeightDiffHalf
      expanded -> 0.dp
    }
  }

  val elevationAnimated by transition.animateDp(transitionSpecDp) { targetState -> elevation.stateValue(targetState) }
  val backgroundAnimated by transition.animateColor(transitionSpecColor) { targetState -> backgroundColor.stateValue(targetState) }
  val cornerRadiusAnimated by transition.animateDp(transitionSpecDp) { targetState -> cornerRadius.stateValue(targetState) }

  Box(modifier = modifier
    .offset(xOffset, yOffset)
    .zIndex(if (buttonState.targetState == expanded || buttonState.currentState == expanded) expandedZIndex else unexpandedZIndex)
    .padding(end = endPaddingAnimated, bottom = bottomPaddingAnimated)
    .size(widthAnimated + endPaddingAnimated, heightAnimated + bottomPaddingAnimated)
  ) {
    var cardModifier = Modifier.size(widthAnimated, heightAnimated)
    if (expandable) cardModifier = cardModifier.pointerInput(Unit, pointerBlock(buttonState, expandedStateFlow, clickableState))
    if (!expandable && unexpandableClickable != null) cardModifier = cardModifier.clickable(onClick = unexpandableClickable)
    Card(
      backgroundColor = backgroundAnimated,
      elevation = elevationAnimated,
      shape = RoundedCornerShape(cornerRadiusAnimated),
      border = border,
      modifier = cardModifier
    ) {
      content?.invoke(transition)
      if (text != null) basicTextContent(transition, text, textColor, textSize, size.stateValue(unpressed).height)
    }
  }
}

@Composable
fun basicTextContent(
  transition: Transition<PressExpandButtonState>,
  text: String,
  textColor: PressExpandButtonState.Map<Color>,
  textSize: PressExpandButtonState.Map<Int>,
  height: Dp,
) {

  val textColorAnimated by transition.animateColor(transitionSpecColor) { targetState -> textColor.stateValue(targetState) }
  val textSizeAnimated by transition.animateInt(transitionSpecInt) { targetState -> textSize.stateValue(targetState) }
  val textYOffsetAnimated by transition.animateDp(transitionSpecDp) { targetState ->
    when(targetState) {
      unpressed -> (height - (with(LocalDensity.current) { textSize.unpressed.sp.toDp() })) * .5f
      pressed -> (height - (with(LocalDensity.current) { textSize.pressed.sp.toDp() })) * .5f
      expanded -> 8.dp + topWindowInset()
    }
  }

  Text(
    textAlign = TextAlign.Center,
    text = text,
    fontSize = textSizeAnimated.sp,
    fontWeight = FontWeight.Normal,
    color = textColorAnimated,
    modifier = Modifier
      .offset(0.dp, textYOffsetAnimated)
  )
}

private fun pointerBlock(
  buttonState: MutableTransitionState<PressExpandButtonState>,
  expandedStateFlow: MutableStateFlow<Boolean>? = null,
  clickable: State<Boolean>? = null,
): suspend PointerInputScope.() -> Unit = {
  detectTapGestures(
    onPress = {
      if (clickable?.value == false) return@detectTapGestures

      try {
        if (buttonState.currentState == unpressed) {
          buttonState.targetState = pressed
        }

        awaitRelease()
        if (buttonState.currentState == pressed || buttonState.targetState == pressed) {
          buttonState.targetState = expanded
          expandedStateFlow?.value = true
        } else if (buttonState.currentState == expanded || buttonState.targetState == expanded) {
//          if (expandedState == null) buttonState.targetState = unpressed
        }
      } catch (cancel: CancellationException) {
        if (buttonState.currentState == pressed || buttonState.targetState == pressed) {
          buttonState.targetState = unpressed
        }
      }
    },
  )
}