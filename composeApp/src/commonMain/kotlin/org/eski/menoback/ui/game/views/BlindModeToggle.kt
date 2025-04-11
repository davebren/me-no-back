package org.eski.menoback.ui.game.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.menoback.ui.game.vm.GameState
import org.eski.ui.icons.Shovel

@Composable
fun BlindModeToggle(
  vm: GameScreenViewModel,
  modifier: Modifier = Modifier
) {
  val enabled by vm.blindModeEnabled.collectAsState()
  val gameState by vm.gameState.collectAsState()
  if (gameState == GameState.Running) return

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.fillMaxWidth()
  ) {
    Icon(
      imageVector = Icons.Shovel,
      contentDescription = "Blind Mode",
      tint = if (enabled) Color.Red else Color.Gray
    )

    Spacer(modifier = Modifier.width(8.dp))

    Text(
      text = "Blind Mode",
      color = if (enabled) Color.Red else Color.Gray,
      fontSize = 14.sp,
      modifier = Modifier.weight(1f)
    )

    Switch(
      checked = enabled,
      onCheckedChange = { vm.blindModeToggled() },
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.Red,
        checkedTrackColor = Color.Red.copy(alpha = 0.5f),
        uncheckedThumbColor = Color.Gray,
        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
      )
    )
  }
}