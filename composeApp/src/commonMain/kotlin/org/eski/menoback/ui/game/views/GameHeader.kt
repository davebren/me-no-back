package org.eski.menoback.ui.game.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.menoback.ui.game.vm.GameState
import org.eski.menoback.ui.game.data.GameSettings

@Composable
fun GameHeader(
  vm: GameScreenViewModel,
  gameSettings: GameSettings
) {
  val nbackLevel by vm.nback.level.collectAsState()
  val nbackStreak by vm.nback.streak.collectAsState()
  val nbackMultiplierText by vm.nback.multiplierText.collectAsState()
  val score by vm.score.collectAsState()
  val timeLeft by vm.timeRemaining.collectAsState()
  val timerColor by vm.timerColor.collectAsState()
  val gameState by vm.gameState.collectAsState()
  val gameDuration by gameSettings.gameDuration.collectAsState()
  val highScoreText by vm.currentHighScoreText.collectAsState()

  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "MeNoBack",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = Color.LightGray,
    )

    Spacer(modifier = Modifier.height(8.dp))

    // High score display - only show on start screen
    if (gameState == GameState.NotStarted) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        backgroundColor = Color(0xFF444444),
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp
      ) {
        Text(
          text = highScoreText,
          fontSize = 16.sp,
          fontWeight = FontWeight.Medium,
          color = Color.Yellow,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(vertical = 8.dp)
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      InfoItem(label = "Score", value = score.toString())
      InfoItem(label = "Multiplier", value = nbackMultiplierText)
      InfoItem(label = "$nbackLevel-Back", value = "Streak: $nbackStreak")

      // Timer with +/- buttons, only visible when game is not started
      if (gameState == GameState.NotStarted) {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { gameSettings.decreaseGameDuration() },
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Remove,
              contentDescription = "Decrease Game Duration",
              tint = Color.LightGray,
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          Column(
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Time",
              fontSize = 12.sp,
              color = Color.Gray
            )
            Text(
              text = gameSettings.formatDuration(if (gameState == GameState.NotStarted) gameDuration else timeLeft),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = timerColor,
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          IconButton(
            onClick = { gameSettings.increaseGameDuration() },
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Increase Game Duration",
              tint = Color.LightGray,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      } else {
        // Regular timer display during game
        InfoItem(
          label = "Time",
          value = gameSettings.formatDuration(timeLeft),
          valueTextColor = timerColor
        )
      }
    }
  }
}

@Composable
private fun InfoItem(label: String, value: String, valueTextColor: Color = Color.LightGray) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = label,
      fontSize = 12.sp,
      color = Color.Gray
    )
    Text(
      text = value,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = valueTextColor,
    )
  }
}