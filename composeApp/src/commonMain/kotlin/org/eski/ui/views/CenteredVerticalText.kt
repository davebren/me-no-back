package org.eski.ui.views

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.eski.menoback.PlatformType
import org.eski.menoback.getPlatform


@Composable
fun CenteredVerticalText(
  text: String = "",
  fontSize: TextUnit = 16.sp,
  fontWeight: FontWeight = FontWeight.Normal,
  color: Color = Color.Unspecified,
  style: TextStyle = LocalTextStyle.current,
  fontFamily: FontFamily = FontFamily.Default,
  modifier: Modifier = Modifier,
) {
  var adjustedModifier = modifier
  if (getPlatform().type == PlatformType.desktop) {
    adjustedModifier = modifier.offset(y = with(LocalDensity.current) { -16.sp.toDp() * .1f } ) // Fix issue with desktop text alignment.
  }

  Text(
    text = text,
    fontSize = fontSize,
    textAlign = TextAlign.Center,
    fontWeight = fontWeight,
    color = color,
    style = style,
    fontFamily = fontFamily,
    modifier = adjustedModifier
      .wrapContentHeight()
  )

}