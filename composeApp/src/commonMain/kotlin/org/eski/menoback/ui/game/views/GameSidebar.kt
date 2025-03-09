package org.eski.menoback.ui.game.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.TetriminoColors
import org.eski.menoback.ui.game.model.TetriminoHistory
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.menoback.ui.game.vm.GameState
import org.eski.ui.util.grid
import org.eski.ui.util.grid2

@Composable
fun RowScope.GameSidebar(vm: GameScreenViewModel) {
  val showGameControls by vm.showGameControls.collectAsState()
  val gameState by vm.gameState.collectAsState()

  Column(
    modifier = Modifier.widthIn(100.dp, 300.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    NextPiecePreview(vm, modifier = Modifier.size(120.dp))
    Spacer(modifier = Modifier.height(grid2))

    // Add N-Back Level and Color Mode settings when game is not running
    if (gameState == GameState.NotStarted || gameState == GameState.GameOver) {
      NBackSettingsPanel(vm)
      Spacer(modifier = Modifier.height(grid2))
    }

    if (showGameControls) CollapsibleGameControls(vm)
  }
}

@Composable
fun NBackSettingsPanel(vm: GameScreenViewModel) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
      .background(Color(0xFF3A3A3A), RoundedCornerShape(8.dp))
      .padding(12.dp)
  ) {
    Text(
      text = "N-Back Settings",
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )

    Spacer(modifier = Modifier.height(12.dp))

    NBackLevelSelector(vm)

    Spacer(modifier = Modifier.height(12.dp))

    ColorNbackToggle(vm)
  }
}

@Composable
fun NextPiecePreview(
  vm: GameScreenViewModel,
  modifier: Modifier = Modifier
) {
  val tetriminoColors: TetriminoColors by vm.tetriminoColors.collectAsState()
  val nextTetrimino: TetriminoHistory.Entry? by vm.nextTetrimino.collectAsState()

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Next:",
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = Color.LightGray
    )

    Spacer(modifier = Modifier.height(8.dp))

    Box(
      modifier = Modifier
        .width(104.dp)
        .height(104.dp)
        .border(2.dp, Color.LightGray, RoundedCornerShape(4.dp))
        .padding(grid),
      contentAlignment = Alignment.Center
    ) {
      val tetrimino = nextTetrimino?.tetrimino ?: return@Box
      val color = nextTetrimino?.colorType?.colorIndex?.let { tetriminoColors.fromInt(it) } ?: return@Box // TODO: Handle in VM
      val pieceRows = tetrimino.shape.size
      val pieceCols = tetrimino.shape[0].size

      Column(
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        for (row in 0 until pieceRows) {
          Row {
            for (col in 0 until pieceCols) {
              Box(
                modifier = Modifier
                  .size(20.dp)
                  .padding(1.dp)
                  .background(if (tetrimino.shape[row][col] != 0) color else Color.Transparent)
              )
            }
          }
        }
      }
    }
  }
}