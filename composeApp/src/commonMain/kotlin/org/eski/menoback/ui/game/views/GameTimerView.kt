package org.eski.menoback.ui.game.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.data.GameSettings
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.menoback.ui.game.vm.GameState
import org.eski.ui.util.grid
import org.eski.ui.util.grid3
import org.eski.ui.util.gridHalf
import org.eski.ui.util.gridPlusHalf
import org.eski.ui.views.CenteredVerticalText

@Composable
fun Timer(
  modifier: Modifier = Modifier,
  vm: GameScreenViewModel,
  gameSettings: GameSettings,
) {
  val gameState by vm.gameState.collectAsState()
  val timerColor by vm.timerColor.collectAsState()
  val timeLeft by vm.timeRemaining.collectAsState()
  val gameDuration by gameSettings.gameDuration.collectAsState()

  // Timer with +/- buttons, only visible when game is not started
  if (gameState == GameState.NotStarted) {
    Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "Time",
        fontSize = 14.sp,
        color = Color.Gray
      )
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { gameSettings.decreaseGameDuration() },
          modifier = Modifier.size(grid3)
            .align(alignment = Alignment.CenterVertically)
            .clickable { gameSettings.decreaseGameDuration() }
            .background(color = Color.White, shape = CircleShape)
            .padding(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Remove,
            contentDescription = "Decrease Game Duration",
            tint = Color.DarkGray,
            modifier = Modifier.size(16.dp)
          )
        }
        Spacer(modifier = Modifier.width(gridPlusHalf))
        CenteredVerticalText(
          modifier = Modifier.widthIn(min = 96.dp),
          text = gameSettings.formatDuration(if (gameState == GameState.NotStarted) gameDuration else timeLeft),
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = timerColor,
        )
        Spacer(modifier = Modifier.width(gridPlusHalf))
        IconButton(
          onClick = { gameSettings.increaseGameDuration() },
          modifier = Modifier.size(grid3)
            .align(alignment = Alignment.CenterVertically)
            .clickable { gameSettings.increaseGameDuration() }
            .background(color = Color.White, shape = CircleShape)
            .padding(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Increase Game Duration",
            tint = Color.DarkGray,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  } else {
    // Regular timer display during game.
    Text(
      modifier = modifier,
      text = gameSettings.formatDuration(timeLeft),
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = timerColor,
    )
  }
}