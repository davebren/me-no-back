package org.eski.ui.views

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun spacer(size: Dp) {
  Spacer(modifier = Modifier.size(size)
  )
}

@Composable
fun spacer(height: Dp = 0.dp, width: Dp = 0.dp ) {
  Spacer(modifier = Modifier
    .height(height)
    .width(width)
  )
}

@Composable
fun topWindowInsetSpacer() {
    val topMargin = with(LocalDensity.current) {
      WindowInsets.safeDrawing.getTop(this).toDp()
    }
    spacer(height = topMargin)
}