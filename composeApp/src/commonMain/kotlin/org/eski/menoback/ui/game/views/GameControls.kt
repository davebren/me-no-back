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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.model.Rotation
import org.eski.menoback.ui.game.vm.GameScreenViewModel


@Composable
fun GameControls(
  vm: GameScreenViewModel,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // N-Back controls
    NBackControls(vm)

    // Rotate button
    Button(
      onClick = { vm.rotatePiece(Rotation.clockwise) },
      modifier = Modifier.fillMaxWidth()
    ) {
      Icon(Icons.Filled.Refresh, contentDescription = "Rotate")
//            Icon(Icons.Filled.RotateRight, contentDescription = "Rotate")
      Spacer(modifier = Modifier.width(8.dp))
      Text("Rotate")
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Left/Right buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      Button(
        onClick = { vm.leftClicked() },
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Move Left")
      }

      Spacer(modifier = Modifier.width(8.dp))

      Button(
        onClick = { vm.rightClicked() },
        modifier = Modifier.weight(1f)
      ) {
        Icon(Icons.Filled.ArrowForward, contentDescription = "Move Right")
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Drop button
    Button(
      onClick = { vm.dropPiece() },
      modifier = Modifier.fillMaxWidth(),
      colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
    ) {
      Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Drop", tint = Color.White)
//            Icon(Icons.Filled.ArrowDownward, contentDescription = "Drop")
      Spacer(modifier = Modifier.width(8.dp))
      Text("Drop", color = Color.White)
    }
  }
}

@Composable
fun NBackControls(
  vm: GameScreenViewModel,
) {
  val nbackLevel by vm.nback.level.collectAsState()

  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "$nbackLevel-Back Match?",
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = Color.LightGray,
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
      Button(
        onClick = { vm.nbackMatchChoice() },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
      ) {
        Text("Match")
      }
    }
  }
}