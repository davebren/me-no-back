package org.eski.menoback.ui.game.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
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

@Composable
fun NBackLevelSelector(
  vm: GameScreenViewModel,
  modifier: Modifier = Modifier
) {
  val nbackLevel by vm.nback.level.collectAsState()

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "n-Back Level",
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = Color.LightGray
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Button(
        onClick = { vm.nback.decreaseLevel() },
        modifier = Modifier.size(40.dp)
      ) {
        Icon(Icons.Default.Remove, contentDescription = "Decrease N-Back Level")
      }

      Spacer(modifier = Modifier.width(8.dp))

      Text(
        text = nbackLevel.toString(),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = { vm.nback.increaseLevel() },
        modifier = Modifier.size(40.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = "Increase N-Back Level")
      }
    }
  }
}