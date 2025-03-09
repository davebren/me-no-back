package org.eski.menoback.ui.game.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.data.NbackProgressData
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.menoback.ui.game.vm.GameState

@Composable
fun NBackLevelSelector(
  vm: GameScreenViewModel,
  modifier: Modifier = Modifier
) {
  val nbackLevel by vm.nback.level.collectAsState()
  val maxLevel by vm.nback.maxLevel.collectAsState()
  val gameState by vm.gameState.collectAsState()
  val isAtMaxLevel = nbackLevel >= maxLevel

  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "n-Back Level",
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = Color.LightGray
      )

      if (isAtMaxLevel && gameState == GameState.NotStarted) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "(Max)",
          fontSize = 12.sp,
          color = Color.Yellow
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth()
    ) {
      Button(
        onClick = { vm.nback.decreaseLevel() },
        modifier = Modifier.size(40.dp),
        colors = ButtonDefaults.buttonColors(
          backgroundColor = Color(0xFF444444)
        )
      ) {
        Icon(
          Icons.Default.Remove,
          contentDescription = "Decrease N-Back Level",
          tint = Color.White
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Text(
        text = nbackLevel.toString(),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )

      Spacer(modifier = Modifier.width(16.dp))

      Button(
        onClick = { vm.nback.increaseLevel() },
        enabled = nbackLevel < maxLevel,
        modifier = Modifier.size(40.dp),
        colors = ButtonDefaults.buttonColors(
          backgroundColor = if (nbackLevel < maxLevel) Color(0xFF444444) else Color(0xFF333333)
        )
      ) {
        if (nbackLevel < maxLevel) {
          Icon(
            Icons.Default.Add,
            contentDescription = "Increase N-Back Level",
            tint = Color.White
          )
        } else {
          Icon(
            Icons.Default.Lock,
            contentDescription = "Level Locked",
            tint = Color.Gray
          )
        }
      }
    }

    if (isAtMaxLevel && gameState == GameState.NotStarted) {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Play at this level with ${NbackProgressData.accuracyThreshold}%+ accuracy to unlock the next level",
        fontSize = 12.sp,
        color = Color.LightGray,
        modifier = Modifier.padding(horizontal = 8.dp)
      )
    }
  }
}