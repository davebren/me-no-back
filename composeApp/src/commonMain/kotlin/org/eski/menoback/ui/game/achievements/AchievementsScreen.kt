package org.eski.menoback.ui.game.achievements

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.selects.select
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun AchievementsScreen(
    onDismiss: () -> Unit,
    vm: AchievementsViewModel,
) {
    val selectedType by vm.typeDisplayed.collectAsState()
    val tabs by vm.tabs.collectAsState()
    val displayedAchievements by vm.displayedAchievements.collectAsState()
    
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
                        text = "Achievements",
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
                
                // Achievement category tabs
                ScrollableTabRow(
                    selectedTabIndex = tabs.indexOf(selectedType),
                    backgroundColor = Color(0xFF444444),
                    contentColor = Color.White,
                    edgePadding = 0.dp,
                    divider = { Divider(color = Color.Transparent, thickness = 0.dp) }
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedType == tab
                        Tab(
                            selected = isSelected,
                            onClick = { vm.typeClicked(tab) },
                            text = {
                                Text(
                                    text = formatTabName(tab),
                                    color = if (isSelected) Color.Cyan else Color.LightGray,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                if (displayedAchievements.isEmpty()) {
                    EmptyAchievementsView()
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(displayedAchievements) { achievement ->
                            AchievementItem(achievement = achievement)
                        }
                    }
                }
                AchievementsFooter(vm)
            }
        }
    }
}

@Composable
private fun EmptyAchievementsView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No achievements in this category yet",
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AchievementsFooter(vm: AchievementsViewModel) {
    val progress by vm.achievementProgress.collectAsState()
    val completionPercentage by vm.achievementPercentageText.collectAsState()
    val totalCount by vm.totalAchievementCount.collectAsState()
    val unlockedCount by vm.unlockedAchievementCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Divider(
            color = Color.Gray.copy(alpha = 0.5f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progress: $unlockedCount/$totalCount achievements",
                color = Color.LightGray,
                fontSize = 14.sp
            )
            
            Text(
                text = completionPercentage, // TODO: Format.
                color = Color.LightGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color.Cyan,
            backgroundColor = Color.DarkGray
        )
    }
}

@Composable
fun AchievementIcon(achievement: Achievement, modifier: Modifier = Modifier) {
    var iconModifier = modifier
        .clip(CircleShape)
        .border(
            width = 2.dp,
            color = if (!achievement.unlocked) Color.Gray else Color.Yellow,
            shape = CircleShape
        )
    iconModifier = if (!achievement.unlocked) iconModifier.background(Color.DarkGray)
        else iconModifier.background(Brush.radialGradient(
            colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500)),
            radius = 48f
        ))
    
    Box(
        modifier = iconModifier,
        contentAlignment = Alignment.Center
    ) {
        val icon = achievement.icon
        val tint = if (!achievement.unlocked) Color.Gray.copy(alpha = 0.5f) else Color.White
        
        Icon(
            imageVector = icon,
            contentDescription = achievement.title,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
        
        if (!achievement.unlocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color.Gray,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.DarkGray, CircleShape)
                    .padding(2.dp)
            )
        }
    }
}

@Composable
fun AchievementUnlockedBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                ),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "UNLOCKED",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

//@Composable
//fun getIconForAchievement(achievement: Achievement) = when (achievement.type) {
//    AchievementType.LEVEL_PROGRESSION -> Icons.Default.Psychology
//    AchievementType.HIGH_SCORE -> Icons.Default.EmojiEvents
//    AchievementType.STREAK -> Icons.Default.Bolt
//    AchievementType.ACCURACY -> Icons.Default.MyLocation
//    AchievementType.GAME_COUNT -> Icons.Default.SportsEsports
//    AchievementType.SPECIAL -> Icons.Default.Star
//}

private fun formatTabName(type: AchievementType): String = when (type) {
    AchievementType.LEVEL_PROGRESSION -> "Levels"
    AchievementType.HIGH_SCORE -> "Scores"
    AchievementType.ACCURACY -> "Accuracy"
    AchievementType.SPECIAL -> "Special"
}