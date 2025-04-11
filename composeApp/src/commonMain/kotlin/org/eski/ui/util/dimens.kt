package org.eski.ui.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

val grid = 8.dp
val gridHalf = 4.dp
val gridPlusHalf = 12.dp
val grid2 = 16.dp
val grid3 = 24.dp
val grid4 = 32.dp
val grid5 = 40.dp
val grid6 = 48.dp
val grid8 = 64.dp
val grid10 = 80.dp
val grid12 = 96.dp
val grid14 = 112.dp

fun Dp.square(): DpSize = DpSize(this, this)

@Composable
fun topWindowInset(): Dp = with(LocalDensity.current) {
  WindowInsets.safeDrawing.getTop(this).toDp()
}