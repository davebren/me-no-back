package org.eski.menoback.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.eski.ui.util.grid6
import org.eski.ui.util.grid8

@Composable
fun GameIntroDialog(
    isFirstLaunch: Boolean = true,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color(0xFF333333),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFirstLaunch) "Welcome to MeNoBack!" else "How to Play",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
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
                
                // Content scrollable area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    // Game explanation
                    SectionTitle("What is MeNoBack?")
                    
                    Text(
                        text = "MeNoBack is a brain training game that combines classic block stacking with the N-Back memory task. " +
                            "It's designed to improve your working memory and cognitive abilities.",
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    SectionTitle("How to Play")
                    
                    Text(
                        text = "1. The game presents Tetrimino pieces one by one.\n\n" +
                              "2. Your task is to identify when the current piece matches the piece that appeared N positions back:\n" +
                              "   • For 2-back, compare with the piece from 2 moves ago\n" +
                              "   • For 3-back, compare with the piece from 3 moves ago\n\n" +
                              "3. You can match based on shape, color, or both depending on your settings.\n\n" +
                              "4. If you spot a match, press the match button before dropping the current piece.",
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Scoring explanation
                    SectionTitle("Scoring")

                    Text(
                        text = "• Earn points for correct matches and non-matches\n" +
                            "• Building streaks increases your score multiplier\n" +
                            "• Higher N-Back levels give higher scores\n" +
                            "• Clearing lines gives bonus points\n" +
                            "• Accuracy is tracked for each game session",
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    SectionTitle("Game Controls - Keyboard")
                    
                    Text(
                        text = "Keybindings can be modified in Settings, here are the defaults:\n" +
                                "• Left/Right Arrows: Move piece horizontally\n" +
                              "• Down Arrow: Move piece down\n" +
                              "• Up Arrow: Rotate piece 180°\n" +
                              "• X: Rotate piece clockwise\n" +
                              "• Z: Rotate piece counter-clockwise\n" +
                              "• Spacebar: Drop piece\n" +
                              "• C: Shape match\n" +
                              "• V: Color match\n" +
                              "• Enter: Start game\n" +
                              "• Escape: Pause game",
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Game settings explanation
                    SectionTitle("Game Settings")
                    
                    Text(
                        text = "• N-Back Level: Adjust how many positions back you need to remember\n" +
                              "• Color Mode: Toggle matching by color in addition to shape\n" +
                              "• Game Duration: Set how long each game session lasts\n" +
                              "• Visual Feedback: Choose how you receive feedback on matches\n" +
                              "• Controls: Toggle on-screen controls or use keyboard shortcuts",
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                // Footer with buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onOpenSettings,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Gray
                        ),
                        modifier = Modifier.padding(end = 8.dp).height(grid6)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.padding(end = 4.dp),
                            tint = Color.White
                        )
                        Text("Game Settings", color = Color.White)
                    }
                    
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF4CAF50)
                        ),
                        modifier = Modifier.height(grid6)
                    ) {
                        Text("Let's Play!", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.Cyan,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun GameInfoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Game Information",
            tint = Color.White
        )
    }
}