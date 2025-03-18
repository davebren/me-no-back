package org.eski.menoback.ui.game.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.ui.util.grid
import org.eski.ui.util.grid2
import org.eski.ui.util.grid6
import org.eski.ui.util.gridHalf
import org.eski.ui.views.spacer

@Composable
fun ActionBarMenu(
  vm: GameScreenViewModel
) {

  Box(modifier = Modifier.fillMaxWidth()) {
    LeftButtons(vm, Modifier.align(alignment = Alignment.CenterStart))
    Column(Modifier.align(alignment = Alignment.Center)) {
      Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
        Icon(
          modifier = Modifier.align(alignment = Alignment.CenterVertically).size(12.dp),
          imageVector = Icons.Filled.Favorite, contentDescription = "",
          tint = Color.Red
        )
        spacer(width = grid)
        Text(
          text = "MeNoBack",
          fontSize = 24.sp,
          color = Color.White,
        )
        spacer(width = grid)
        Icon(
          modifier = Modifier.align(alignment = Alignment.CenterVertically).size(12.dp),
          imageVector = Icons.Filled.Favorite, contentDescription = "",
          tint = Color.Red
        )
      }
      Text(
        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        text = "by David Breneisen",
        fontSize = 12.sp,
        color = Color.LightGray,
      )
    }

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