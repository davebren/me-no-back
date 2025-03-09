package org.eski.menoback.ui.game.achievements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private const val NOTIFICATION_DURATION_MS = 5000L

@Composable
fun AchievementNotification(
    achievement: Achievement?,
    onDismiss: () -> Unit,
    onViewAchievements: () -> Unit
) {
    var visible by remember { mutableStateOf(achievement != null) }
    
    LaunchedEffect(achievement) {
        if (achievement != null) {
            visible = true
            delay(NOTIFICATION_DURATION_MS)
            visible = false
            delay(500) // Animation duration
            onDismiss()
        }
    }
    
    AnimatedVisibility(
        visible = visible && achievement != null,
        enter = fadeIn(animationSpec = tween(300)) +
                slideInHorizontally(animationSpec = tween(500)) { fullWidth -> fullWidth },
        exit = fadeOut(animationSpec = tween(300)) +
                slideOutHorizontally(animationSpec = tween(500)) { fullWidth -> fullWidth }
    ) {
        achievement?.let {
            AchievementToast(
                achievement = it,
                onDismiss = { visible = false },
                onViewAchievements = onViewAchievements
            )
        }
    }
}

@Composable
private fun AchievementToast(
    achievement: Achievement,
    onDismiss: () -> Unit,
    onViewAchievements: () -> Unit
) {
    val iconScale by animateFloatAsState(
        targetValue = 1.0f,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = 100
        ),
        label = "iconScale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(16.dp)
            .clickable { onViewAchievements() },
        elevation = 8.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFF1D3557)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1D3557),
                            Color(0xFF2A6F97)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.Yellow,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Achievement Unlocked!",
                        color = Color.Yellow,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500)),
                                    radius = 56f
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconForAchievement(achievement),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .scale(iconScale)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = achievement.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = achievement.description,
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Tap to view achievements",
                    color = Color.LightGray.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.7f),
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}