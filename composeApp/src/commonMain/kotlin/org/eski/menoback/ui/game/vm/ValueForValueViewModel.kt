package org.eski.menoback.ui.game.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ValueForValueViewModel(
  scope: CoroutineScope,
  gameState: StateFlow<GameState>
) {
  val buttonVisible = gameState.map {
    it == GameState.NotStarted
  }.stateIn(scope, SharingStarted.WhileSubscribed(), true)

  val menuShowing = MutableStateFlow<Boolean>(false)

  fun clicked() { menuShowing.value = true }
  fun dismissed() { menuShowing.value = false }
}