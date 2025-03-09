package org.eski.menoback.ui.game.achievements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
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
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.eski.ui.util.grid
import org.eski.ui.util.grid2

@Composable
fun AchievementsScreen(
    achievements: AchievementCollection,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AchievementType.LEVEL_PROGRESSION) }
    val tabs = remember { AchievementType.values().toList() }
    
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
                    selectedTabIndex = tabs.indexOf(selectedTab),
                    backgroundColor = Color(0xFF444444),
                    contentColor = Color.White,
                    edgePadding = 0.dp,
                    divider = { Divider(color = Color.Transparent, thickness = 0.dp) }
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
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
                
                // Achievement list
                val achievementsForTab = achievements.getAchievementsByType(selectedTab)
                    .sortedWith(compareBy({ it.isLocked }, { -it.requiredValue }))
                
                if (achievementsForTab.isEmpty()) {
                    EmptyAchievementsView()
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(achievementsForTab) { achievement ->
                            AchievementItem(achievement = achievement)
                        }
                    }
                }
                
                // Footer with stats
                AchievementsFooter(achievements)
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
private fun AchievementsFooter(achievements: AchievementCollection) {
    val allAchievements = achievements.getAllAchievements()
    val unlockedCount = allAchievements.count { !it.isLocked }
    val totalCount = allAchievements.size
    val completionPercentage = if (totalCount > 0) (unlockedCount.toFloat() / totalCount.toFloat()) * 100 else 0f
    
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
                text = completionPercentage.toString(), // TODO: Format.
                color = if (completionPercentage > 50) Color.Cyan else Color.LightGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = completionPercentage / 100,
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
fun AchievementItem(achievement: Achievement) {
    val progressAnimation by animateFloatAsState(
        targetValue = achievement.progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )
    
    val backgroundColor = if (achievement.isLocked) {
        Color(0xFF444444)
    } else {
        Color(0xFF1E5F74)
    }
    
    Card(
        backgroundColor = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AchievementIcon(
                        achievement = achievement,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = achievement.title,
                            color = if (achievement.isLocked) Color.Gray else Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Text(
                            text = achievement.description,
                            color = if (achievement.isLocked) Color.Gray.copy(alpha = 0.7f) else Color.LightGray,
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        if (achievement.isLocked && achievement.type != AchievementType.SPECIAL) {
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            LinearProgressIndicator(
                                progress = progressAnimation,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = Color.Cyan,
                                backgroundColor = Color.DarkGray.copy(alpha = 0.5f)
                            )
                            
                            Text(
                                text = "${(progressAnimation * 100).toInt()}%",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                    
                    if (!achievement.isLocked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = Color.Green,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Show completion date for unlocked achievements
                if (!achievement.isLocked && achievement.completedTimestamp != null) {
                    Text(
                        text = "Completed ${formatTimestamp(achievement.completedTimestamp)}",
                        color = Color.LightGray.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
            
            if (achievement.isLocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0x40000000))
                )
            }
        }
    }
}

@Composable
private fun AchievementIcon(achievement: Achievement, modifier: Modifier = Modifier) {
    var iconModifier = modifier
        .clip(CircleShape)
        .border(
            width = 2.dp,
            color = if (achievement.isLocked) Color.Gray else Color.Yellow,
            shape = CircleShape
        )
    iconModifier = if (achievement.isLocked) iconModifier.background(Color.DarkGray)
        else iconModifier.background(Brush.radialGradient(
            colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500)),
            radius = 48f
        ))
    
    Box(
        modifier = iconModifier,
        contentAlignment = Alignment.Center
    ) {
        val icon = getIconForAchievement(achievement)
        val tint = if (achievement.isLocked) Color.Gray.copy(alpha = 0.5f) else Color.White
        
        Icon(
            imageVector = icon,
            contentDescription = achievement.title,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
        
        if (achievement.isLocked) {
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

@Composable
fun getIconForAchievement(achievement: Achievement) = when (achievement.type) {
    AchievementType.LEVEL_PROGRESSION -> Icons.Default.Psychology
    AchievementType.HIGH_SCORE -> Icons.Default.EmojiEvents
    AchievementType.STREAK -> Icons.Default.Bolt
    AchievementType.ACCURACY -> Icons.Default.MyLocation
    AchievementType.GAME_COUNT -> Icons.Default.SportsEsports
    AchievementType.SPECIAL -> Icons.Default.Star
}

private fun formatTabName(type: AchievementType): String = when (type) {
    AchievementType.LEVEL_PROGRESSION -> "Levels"
    AchievementType.HIGH_SCORE -> "Scores"
    AchievementType.STREAK -> "Streaks"
    AchievementType.ACCURACY -> "Accuracy"
    AchievementType.GAME_COUNT -> "Games"
    AchievementType.SPECIAL -> "Special"
}

private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val now = Clock.System.now()
    val diff = now.toEpochMilliseconds() - timestamp
    
    return when {
//        diff < TimeUnit.MINUTES.toMillis(1) -> "just now"
//        diff < TimeUnit.HOURS.toMillis(1) -> "${diff / TimeUnit.MINUTES.toMillis(1)} minutes ago"
//        diff < TimeUnit.DAYS.toMillis(1) -> "${diff / TimeUnit.HOURS.toMillis(1)} hours ago"
//        diff < TimeUnit.DAYS.toMillis(7) -> "${diff / TimeUnit.DAYS.toMillis(1)} days ago"
        else -> {
            val date = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            "${date.month.name.lowercase().capitalize()} ${date.dayOfMonth}, ${date.year}"
        }
    }
}