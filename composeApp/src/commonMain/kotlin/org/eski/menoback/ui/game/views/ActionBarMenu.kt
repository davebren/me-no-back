package org.eski.menoback.ui.game.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.ui.util.grid6
import org.eski.ui.util.gridHalf

@Composable
fun ActionBarMenu(
  vm: GameScreenViewModel
) {

  Box(modifier = Modifier.fillMaxWidth()) {
    LeftButtons(vm, Modifier.align(alignment = Alignment.CenterStart))
    Text(
      modifier = Modifier.align(alignment = Alignment.Center),
      text = "MeNoBack",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = Color.LightGray,
    )
    RightButtons(vm, Modifier.align(alignment = Alignment.CenterEnd))
  }
}

@Composable private fun LeftButtons(vm: GameScreenViewModel, modifier: Modifier) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = gridHalf),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    IconButton(
      modifier = Modifier
        .size(grid6)
        .padding(gridHalf),
      onClick = { vm.options.introClicked() }
    ) {
      Icon(
        imageVector = Icons.Default.Info,
        contentDescription = "Game Information",
        tint = Color.White,
      )
    }
  }
}

@Composable private fun RightButtons(vm: GameScreenViewModel, modifier: Modifier) {
  Row(
    modifier = modifier
      .padding(horizontal = gridHalf)
  ) {

    IconButton(
      modifier = Modifier
        .size(grid6)
        .padding(gridHalf),
      onClick = { vm.options.achievementsClicked() }
    ) {
      Icon(
        imageVector = Icons.Default.EmojiEvents,
        contentDescription = "Achievements",
        tint = Color.White,
      )
    }

    IconButton(
      modifier = Modifier
        .size(grid6)
        .padding(gridHalf),
      onClick = { vm.options.settingsClicked() }
    ) {
      Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = "Settings",
        tint = Color.White,
      )
    }
  }
}