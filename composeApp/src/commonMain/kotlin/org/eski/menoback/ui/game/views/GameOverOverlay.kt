package org.eski.menoback.ui.game.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.eski.menoback.ui.game.data.GameStatsData
import org.eski.menoback.ui.game.model.MatchStats
import org.eski.menoback.ui.game.vm.GameScreenViewModel
import org.eski.menoback.ui.game.vm.GameState

@Composable
fun GameOverOverlay(
    vm: GameScreenViewModel,
    modifier: Modifier = Modifier
) {
    val gameState by vm.gameState.collectAsState()
    val score by vm.score.collectAsState()
    val currentHighScore by vm.currentHighScore.collectAsState()
    val matchStats by vm.nback.matchStats.collectAsState()
    val nbackLevel by vm.nback.level.collectAsState()
    val isNewHighScore = score >= currentHighScore && gameState == GameState.GameOver
    val showLevelUp by vm.nback.showLevelUnlocked.collectAsState()
    val decisionsRequired by vm.nback.decisionsRequired.collectAsState()

    // Only display the overlay when the game is over
    AnimatedVisibility(
        visible = gameState == GameState.GameOver,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.Black.copy(alpha = 0.7f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isNewHighScore) {
                NewHighScoreContent(
                    score = score,
                    matchStats = matchStats,
                    nbackLevel = nbackLevel,
                    showLevelUp = showLevelUp,
                    decisionsRequired = decisionsRequired,
                )
            } else {
                GameOverContent(
                    score = score,
                    highScore = currentHighScore,
                    matchStats = matchStats,
                    nbackLevel = nbackLevel,
                    showLevelUp = showLevelUp,
                    decisionsRequired = decisionsRequired,
                )
            }
        }
    }
}

@Composable
private fun NewHighScoreContent(
    score: Long,
    matchStats: MatchStats,
    nbackLevel: Int,
    showLevelUp: Boolean,
    decisionsRequired: Int,
) {
    Card(
        backgroundColor = Color(0xFF333333),
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "NEW HIGH SCORE!",
                color = Color.Yellow,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$score",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            MatchStatisticsSection(matchStats, nbackLevel, decisionsRequired)

            if (showLevelUp) {
                LevelUpNotification(nbackLevel)
            }
        }
    }
}

@Composable
private fun GameOverContent(
    score: Long,
    highScore: Long,
    matchStats: MatchStats,
    nbackLevel: Int,
    showLevelUp: Boolean,
    decisionsRequired: Int,
) {
    Card(
        backgroundColor = Color(0xFF333333),
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "GAME OVER",
                color = Color.Red,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Score: $score",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "High Score: $highScore",
                color = Color.LightGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Match Statistics Section
            MatchStatisticsSection(matchStats, nbackLevel, decisionsRequired)

            if (showLevelUp) {
                LevelUpNotification(nbackLevel)
            }
        }
    }
}

@Composable
private fun LevelUpNotification(currentLevel: Int) {
    val infiniteTransition = rememberInfiniteTransition()
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Spacer(modifier = Modifier.height(16.dp))

    Divider(color = Color.Yellow.copy(alpha = 0.5f), thickness = 1.dp)

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.LockOpen,
            contentDescription = "Level Unlocked",
            tint = Color.Yellow,
            modifier = Modifier.scale(iconScale)
        )

        Spacer(modifier = Modifier.width(12))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LEVEL UNLOCKED!",
                color = Color.Yellow,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "${currentLevel + 1}-Back now available",
                color = Color.Yellow,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(12))

        Icon(
            imageVector = Icons.Default.LockOpen,
            contentDescription = "Level Unlocked",
            tint = Color.Yellow,
            modifier = Modifier.scale(iconScale)
        )
    }
}

@Composable
private fun MatchStatisticsSection(
    matchStats: MatchStats,
    nbackLevel: Int,
    decisionsRequired: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$nbackLevel-Back Match Stats",
                color = Color.Cyan,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            val accuracy = matchStats.accuracyPercentage
            val accentColor = when {
                accuracy >= GameStatsData.accuracyThreshold -> Color.Green
                accuracy >= GameStatsData.accuracyThreshold * 0.8f -> Color.Yellow
                else -> Color.Red
            }

            Text(
                text = matchStats.formatAccuracy(),
                color = accentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Matches:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Left
        )
        StatRow("✓ Correct", matchStats.correctMatches.toString(), Color.Green.copy(alpha = 0.7f))
        StatRow("✗ Missed", matchStats.missedMatches.toString(), Color.Yellow.copy(alpha = 0.7f))

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Non-Matches:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Left
        )
        StatRow("✓ Correct", matchStats.correctNonMatches.toString(), Color.Green.copy(alpha = 0.7f))
        StatRow("✗ Incorrect", matchStats.incorrectMatches.toString(), Color.Red.copy(alpha = 0.7f))

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        StatRow("Overall Accuracy", matchStats.formatAccuracy(), Color.Cyan)
        StatRow("Total Decisions", matchStats.totalDecisions.toString(), Color.White)

        if (matchStats.accuracyPercentage < GameStatsData.accuracyThreshold) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Need ${GameStatsData.accuracyThreshold}% accuracy and $decisionsRequired match decisions " +
                    "to unlock level ${nbackLevel + 1}",
                color = Color.LightGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.LightGray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun Modifier.width(dp: Int) = this.then(Modifier.padding(horizontal = dp.dp))