package org.eski.menoback.ui.game.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.menoback.ui.game.vm.GameState
import org.eski.ui.util.grid
import org.eski.ui.views.CenteredVerticalText

@Composable
fun GameHeader(
  vm: GameScreenViewModel,
) {
  val gameState by vm.gameState.collectAsState()
  val highScoreText by vm.currentHighScoreText.collectAsState()

  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    HighScore(gameState, highScoreText)
  }
}

@Composable
private fun HighScore(gameState: GameState, highScoreText: String) {
  // High score display - only show on start screen
  if (gameState == GameState.NotStarted) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .widthIn(min = 256.dp)
        .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
        .background(Color(0xFF3A3A3A), RoundedCornerShape(8.dp))
        .padding(grid)
    ) {
      CenteredVerticalText(
        text = highScoreText,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Yellow,
      )
    }
  }
}

@Composable
fun HeaderInfoItem(
  modifier: Modifier = Modifier,
  label: String,
  value: String,
  valueTextColor: Color = Color.LightGray
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    CenteredVerticalText(
      text = label,
      fontSize = 14.sp,
      color = Color.Gray
    )
    CenteredVerticalText(
      text = value,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = valueTextColor,
    )
  }
}