package org.eski.menoback.ui.keybinding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.eski.menoback.ui.game.data.GameSettings
import org.eski.menoback.ui.game.model.FeedbackMode
import org.eski.util.getKeyName

@Composable
fun KeyBindingSettingsDialog(
  keyBindingSettings: KeyBindingSettings,
  gameSettings: GameSettings,
  onDismiss: () -> Unit,
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      dismissOnBackPress = true,
      dismissOnClickOutside = true,
      usePlatformDefaultWidth = false
    )
  ) {
    KeyBindingSettingsDialogContent(
      keyBindingSettings = keyBindingSettings,
      gameSettings = gameSettings,
      onDismiss = onDismiss
    )
  }
}

@Composable
private fun KeyBindingSettingsDialogContent(
  keyBindingSettings: KeyBindingSettings,
  gameSettings: GameSettings,
  onDismiss: () -> Unit
) {
  val scrollState = rememberScrollState()

  val moveLeft by keyBindingSettings.moveLeft.collectAsState()
  val moveRight by keyBindingSettings.moveRight.collectAsState()
  val moveDown by keyBindingSettings.moveDown.collectAsState()
  val rotateClockwise by keyBindingSettings.rotateClockwise.collectAsState()
  val rotateCounterClockwise by keyBindingSettings.rotateCounterClockwise.collectAsState()
  val rotate180 by keyBindingSettings.rotate180.collectAsState()
  val dropPiece by keyBindingSettings.dropPiece.collectAsState()
  val nbackShapeMatch by keyBindingSettings.nbackShapeMatch.collectAsState()
  val nbackColorMatch by keyBindingSettings.nbackColorMatch.collectAsState()
  val startGame by keyBindingSettings.startGame.collectAsState()
  val pauseGame by keyBindingSettings.pauseGame.collectAsState()

  // Get current feedback mode
  val feedbackMode by gameSettings.feedbackMode.collectAsState()

  var selectedBinding by remember { mutableStateOf<String?>(null) }
  val focusRequester = remember { FocusRequester() }

  Card(
    modifier = Modifier
      .fillMaxWidth(0.8f)
      .fillMaxHeight(0.9f),
    shape = RoundedCornerShape(16.dp),
    backgroundColor = Color(0xFF333333),
    elevation = 8.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Game Settings",
          color = Color.White,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { keyBindingSettings.resetToDefaults() }) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reset to Defaults",
            tint = Color.White
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.White
          )
        }
      }

      Divider(
        color = Color.Gray.copy(alpha = 0.5f),
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 8.dp)
      )

      // Key binding list
      Column(
        modifier = Modifier
          .weight(1f)
          .verticalScroll(scrollState)
      ) {
        // Feedback Mode Section
        SectionHeader(text = "Game Feedback")

        Text(
          text = "Visual Feedback Mode",
          color = Color.White,
          fontSize = 15.sp,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FeedbackOptionButton(
            text = "None",
            isSelected = feedbackMode == FeedbackMode.none,
            onClick = { gameSettings.setFeedbackMode(FeedbackMode.none) }
          )

          FeedbackOptionButton(
            text = "Flash Background",
            isSelected = feedbackMode == FeedbackMode.flashBackground,
            onClick = { gameSettings.setFeedbackMode(FeedbackMode.flashBackground) }
          )
        }

        // Keyboard Controls Section
        SectionHeader(text = "Keyboard Controls")

        Text(
          text = "Click on a control to rebind it",
          color = Color.White.copy(alpha = 0.7f),
          fontSize = 14.sp,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(text = "Game Controls")

        KeyBindingRow(
          label = "Start Game",
          keyName = getKeyName(startGame),
          isSelected = selectedBinding == "startGame",
          onClick = { selectedBinding = "startGame" }
        )

        KeyBindingRow(
          label = "Pause Game",
          keyName = getKeyName(pauseGame),
          isSelected = selectedBinding == "pauseGame",
          onClick = { selectedBinding = "pauseGame" }
        )

        SectionHeader(text = "Movement")

        KeyBindingRow(
          label = "Move Left",
          keyName = getKeyName(moveLeft),
          isSelected = selectedBinding == "moveLeft",
          onClick = { selectedBinding = "moveLeft" }
        )

        KeyBindingRow(
          label = "Move Right",
          keyName = getKeyName(moveRight),
          isSelected = selectedBinding == "moveRight",
          onClick = { selectedBinding = "moveRight" }
        )

        KeyBindingRow(
          label = "Move Down",
          keyName = getKeyName(moveDown),
          isSelected = selectedBinding == "moveDown",
          onClick = { selectedBinding = "moveDown" }
        )

        KeyBindingRow(
          label = "Drop Piece",
          keyName = getKeyName(dropPiece),
          isSelected = selectedBinding == "dropPiece",
          onClick = { selectedBinding = "dropPiece" }
        )

        SectionHeader(text = "Rotation")

        KeyBindingRow(
          label = "Rotate Clockwise",
          keyName = getKeyName(rotateClockwise),
          isSelected = selectedBinding == "rotateClockwise",
          onClick = { selectedBinding = "rotateClockwise" }
        )

        KeyBindingRow(
          label = "Rotate Counter-Clockwise",
          keyName = getKeyName(rotateCounterClockwise),
          isSelected = selectedBinding == "rotateCounterClockwise",
          onClick = { selectedBinding = "rotateCounterClockwise" }
        )

        KeyBindingRow(
          label = "Rotate 180°",
          keyName = getKeyName(rotate180),
          isSelected = selectedBinding == "rotate180",
          onClick = { selectedBinding = "rotate180" }
        )

        SectionHeader(text = "N-Back")

        KeyBindingRow(
          label = "N-Back Shape Match",
          keyName = getKeyName(nbackShapeMatch),
          isSelected = selectedBinding == "nbackShapeMatch",
          onClick = { selectedBinding = "nbackShapeMatch" }
        )
        KeyBindingRow(
          label = "N-Back Color Match",
          keyName = getKeyName(nbackColorMatch),
          isSelected = selectedBinding == "nbackColorMatch",
          onClick = { selectedBinding = "nbackColorMatch" }
        )
      }

      if (selectedBinding != null) {
        Divider(
          color = Color.Gray.copy(alpha = 0.5f),
          thickness = 1.dp,
          modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
          text = "Press any key to bind to",
          color = Color.Yellow,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          textAlign = TextAlign.Center
        )
      }
    }

    // Invisible box to capture key events when rebinding
    if (selectedBinding != null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .focusRequester(focusRequester)
          .focusable()
          .onFocusChanged {
            if (!it.isFocused && selectedBinding != null) {
              focusRequester.requestFocus()
            }
          }
          .onKeyEvent { event ->
            if (event.type == KeyEventType.KeyDown) {
              val keyCode = event.key.keyCode

              // TODO: Use enums.
              when (selectedBinding) {
                "moveLeft" -> keyBindingSettings.setMoveLeft(keyCode)
                "moveRight" -> keyBindingSettings.setMoveRight(keyCode)
                "moveDown" -> keyBindingSettings.setMoveDown(keyCode)
                "rotateClockwise" -> keyBindingSettings.setRotateClockwise(keyCode)
                "rotateCounterClockwise" -> keyBindingSettings.setRotateCounterClockwise(keyCode)
                "rotate180" -> keyBindingSettings.setRotate180(keyCode)
                "dropPiece" -> keyBindingSettings.setDropPiece(keyCode)
                "nbackShapeMatch" -> keyBindingSettings.setNbackShapeMatch(keyCode)
                "nbackColorMatch" -> keyBindingSettings.setNbackColorMatch(keyCode)
                "startGame" -> keyBindingSettings.setStartGame(keyCode)
                "pauseGame" -> keyBindingSettings.setPauseGame(keyCode)
              }

              selectedBinding = null
              true
            } else {
              false
            }
          }
      )

      LaunchedEffect(selectedBinding) {
        focusRequester.requestFocus()
      }
    }
  }
}

@Composable
fun RowScope.FeedbackOptionButton(
  text: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Button(
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(
      backgroundColor = if (isSelected) Color(0xFF4CAF50) else Color(0xFF666666),
      contentColor = Color.White
    ),
    modifier = Modifier.weight(1f)
  ) {
    Text(text = text)
  }
}

@Composable
fun SectionHeader(text: String) {
  Text(
    text = text,
    color = Color.LightGray,
    fontSize = 14.sp,
    fontWeight = FontWeight.Bold,
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 16.dp, bottom = 8.dp)
  )

  Divider(
    color = Color.Gray.copy(alpha = 0.3f),
    thickness = 1.dp,
    modifier = Modifier.padding(bottom = 8.dp)
  )
}

@Composable
fun KeyBindingRow(
  label: String,
  keyName: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp)
      .height(44.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      color = Color.White,
      fontSize = 15.sp,
      modifier = Modifier.weight(1f)
    )

    Box(
      modifier = Modifier
        .width(160.dp)
        .height(36.dp)
        .border(
          width = 1.dp,
          color = if (isSelected) Color.Yellow else Color.LightGray.copy(alpha = 0.6f),
          shape = RoundedCornerShape(4.dp)
        )
        .background(
          color = if (isSelected) Color(0xFF3A3A3A) else Color(0xFF222222),
          shape = RoundedCornerShape(4.dp)
        )
        .clickable(onClick = onClick),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = keyName,
        color = if (isSelected) Color.Yellow else Color.White,
        fontSize = 14.sp
      )
    }
  }
}