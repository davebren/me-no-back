package org.eski.menoback.ui.game.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.menoback.ui.game.vm.GameState

@Composable
fun GameStatus(
  vm: GameScreenViewModel,
  modifier: Modifier = Modifier,
  gameState: GameState,
  onResetClicked: () -> Unit
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Show n-back level selector only when game is not started
    if (gameState == GameState.NotStarted || gameState == GameState.GameOver) {
      Spacer(modifier = Modifier.height(8.dp))

      NBackLevelSelector(vm)

      Spacer(modifier = Modifier.height(16.dp))
      ColorNbackToggle(vm)

      Spacer(modifier = Modifier.height(16.dp))
    }

    Text(
      text = when (gameState) {
        GameState.NotStarted -> "Press Start to begin"
        GameState.Running -> "Game in progress"
        GameState.Paused -> "Game paused"
        GameState.GameOver -> "Game Over"
      },
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      textAlign = TextAlign.Center,
      color = Color.LightGray,
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Game control buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      Button(
        onClick = onResetClicked,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
      ) {
        Icon(Icons.Filled.Close, contentDescription = "Reset")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Reset")
      }
    }
  }
}