package org.eski.menoback.ui.game.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.vm.ValueForValueViewModel
import org.eski.ui.animation.AnimateView
import org.eski.ui.util.PlatformBackHandler
import org.eski.ui.util.grid2
import org.eski.ui.views.spacer


@Composable fun ValueForValueScreen(vm: ValueForValueViewModel, visible: Boolean) {
  Box(
    modifier = Modifier.fillMaxWidth().padding(grid2)
  ) {

    Column(modifier = Modifier.fillMaxWidth()) {
      Spacer(modifier = Modifier.height(grid2))

      AnimateView(
        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        visible = visible,
        enter = fadeIn(animationSpec = tween(300, 200)),
        exit = fadeOut(animationSpec = tween(150, 0))
      ) {
        Text(
          "Value for Value",
          fontSize = 22.sp,
          color = Color.White,
        )
      }
      spacer(height = grid2)
      AnimateView(
        modifier = Modifier.align(alignment = Alignment.End),
        visible = visible,
        enter = slideInHorizontally(animationSpec = tween(300, 200), initialOffsetX = { width -> width }),
        exit = slideOutHorizontally(targetOffsetX = { width -> width })
      ) {
        Card(
          backgroundColor = Color.Gray,
          modifier = Modifier.fillMaxWidth().padding(grid2)) {
          Text("Description", textAlign = TextAlign.Center, color = Color.White)
        }
      }
    }

    BackButton(vm, visible)
  }
}

@Composable
private fun BackButton(vm: ValueForValueViewModel, visible: Boolean) {
  if (visible) PlatformBackHandler(onBack = { vm.dismissed() })

  AnimatedVisibility(
    visible = visible,
    enter = fadeIn(animationSpec = tween(300, 200)),
    exit = fadeOut(animationSpec = tween(150, 0))
  ) {
    IconButton(onClick = { vm.dismissed() }) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "Back",
        tint = Color.White
      )
    }
  }
}