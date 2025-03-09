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
import androidx.compose.material.icons.filled.ColorLens
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

@Composable
fun ColorNbackToggle(
  vm: GameScreenViewModel,
  modifier: Modifier = Modifier
) {
  val enabled by vm.nback.colorNbackEnabled.collectAsState()
  val gameState by vm.gameState.collectAsState()
  if (gameState == GameState.Running) return

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.fillMaxWidth()
  ) {
    Icon(
      imageVector = Icons.Default.ColorLens,
      contentDescription = "Color Mode",
      tint = if (enabled) Color.Cyan else Color.Gray
    )

    Spacer(modifier = Modifier.width(8.dp))

    Text(
      text = "Color Match",
      color = if (enabled) Color.Cyan else Color.Gray,
      fontSize = 14.sp,
      modifier = Modifier.weight(1f)
    )

    Switch(
      checked = enabled,
      onCheckedChange = { vm.nback.colorNbackToggled() },
      colors = SwitchDefaults.colors(
        checkedThumbColor = Color.Cyan,
        checkedTrackColor = Color.Cyan.copy(alpha = 0.5f),
        uncheckedThumbColor = Color.Gray,
        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
      )
    )
  }
}