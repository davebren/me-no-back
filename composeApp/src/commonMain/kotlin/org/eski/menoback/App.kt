package org.eski.menoback

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.eski.menoback.ui.game.views.GameScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
  MaterialTheme {
    GameScreen()
  }
}