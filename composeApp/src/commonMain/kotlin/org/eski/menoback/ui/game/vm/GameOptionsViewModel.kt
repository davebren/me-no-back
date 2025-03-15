package org.eski.menoback.ui.game.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import org.eski.menoback.data.introSettings

class GameOptionsViewModel(
  scope: CoroutineScope,
  val gameVm: GameScreenViewModel,
) {
  val introShowing = MutableStateFlow<Boolean>(false)
  val settingsShowing = MutableStateFlow<Boolean>(false)
  val achievementsShowing = MutableStateFlow<Boolean>(false)

  init {
    scope.launch {
      introSettings.introShown.takeWhile { !it }.collectLatest {
        introShowing.value = !it
        if (!it) introSettings.setIntroShown(true)
      }
    }
  }
  fun introClicked() {
    introShowing.value = true
    gameVm.pauseBindingInvoked()
  }
  fun introDismissed() {
    introShowing.value = false
    if (gameVm.gameState.value == GameState.Paused) gameVm.resumeGame()
  }

  fun settingsClicked() {
    settingsShowing.value = true
    gameVm.pauseBindingInvoked()
  }
  fun settingsDismissed() {
    settingsShowing.value = false
    if (gameVm.gameState.value == GameState.Paused) gameVm.resumeGame()
  }

  fun achievementsClicked() {
    achievementsShowing.value = true
    gameVm.pauseBindingInvoked()
  }
  fun achievementsDismissed() {
    achievementsShowing.value = false
    if (gameVm.gameState.value == GameState.Paused) gameVm.resumeGame()
  }
}