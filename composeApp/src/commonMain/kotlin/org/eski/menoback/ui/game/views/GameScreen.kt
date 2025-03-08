package org.eski.menoback.ui.game.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.eski.menoback.data.keyBindingSettings
import org.eski.menoback.data.gameStatsData
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.menoback.ui.keybinding.KeyBindingSettings
import org.eski.menoback.ui.keybinding.KeyBindingSettingsDialog
import org.eski.menoback.ui.game.data.GameSettings
import org.eski.menoback.ui.game.data.GameStatsData
import org.eski.menoback.ui.game.model.FeedbackMode
import org.eski.menoback.ui.keybinding.KeyboardInput
import org.eski.ui.util.grid2

@Composable
fun GameScreen(
    keyBindings: KeyBindingSettings = keyBindingSettings,
    gameSettings: GameSettings = org.eski.menoback.data.gameSettings,
    gameStats: GameStatsData = gameStatsData,
    vm: GameScreenViewModel = viewModel {
        GameScreenViewModel(gameSettings, gameStats)
    }
) {
    val gameState by vm.gameState.collectAsState()
    val feedbackMode by vm.feedbackMode.collectAsState()
    var showKeyBindingDialog by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize(0, 0)) }

    KeyboardInput(vm, keyBindings)

    val backgroundModifier = Modifier
    if (feedbackMode == FeedbackMode.flashBackground) backgroundModifier.feedback(vm)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.DarkGray)
            .onSizeChanged { size = it }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(grid2),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeader(vm, gameSettings)
            Spacer(modifier = Modifier.height(grid2))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                GameBoard(vm, modifier = Modifier.fillMaxHeight())
                Spacer(modifier = Modifier.width(16.dp))
                GameSidebar(vm)
            }

            GameStatus(vm, gameState = gameState, onResetClicked = { vm.resetGame() })
        }

        IconButton(
            modifier = Modifier.align(alignment = Alignment.BottomEnd),
            onClick = { showKeyBindingDialog = true }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White
            )
        }

        GameStartButton(vm, true, containerSize = size)
    }

    if (showKeyBindingDialog) {
        KeyBindingSettingsDialog(
            keyBindingSettings = keyBindings,
            gameSettings = gameSettings,
            onDismiss = { showKeyBindingDialog = false }
        )
    }
}