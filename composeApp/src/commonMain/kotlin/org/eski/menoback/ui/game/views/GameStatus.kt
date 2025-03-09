package org.eski.menoback.ui.game.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.menoback.ui.game.vm.GameState

@Composable
fun GameStatus(
  vm: GameScreenViewModel,
  modifier: Modifier = Modifier,
  gameState: GameState,
  onResetClicked: () -> Unit
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Show n-back level selector only when game is not started
    if (gameState == GameState.NotStarted || gameState == GameState.GameOver) {
      Spacer(modifier = Modifier.height(8.dp))

      NBackLevelSelector(vm)

      Spacer(modifier = Modifier.height(16.dp))
      ColorNbackToggle(vm)

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}