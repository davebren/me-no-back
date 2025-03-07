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
import androidx.compose.material.Text
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
    val isNewHighScore = score > currentHighScore && gameState == GameState.GameOver

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
                NewHighScoreContent(score = score, matchStats = matchStats, nbackLevel = nbackLevel)
            } else {
                GameOverContent(score = score, highScore = currentHighScore, matchStats = matchStats, nbackLevel = nbackLevel)
            }
        }
    }
}

@Composable
private fun NewHighScoreContent(score: Long, matchStats: MatchStats, nbackLevel: Int) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        backgroundColor = Color(0xFF333333),
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        modifier = Modifier.scale(scale).padding(16.dp)
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

            // Match Statistics Section
            MatchStatisticsSection(matchStats, nbackLevel)
        }
    }
}

@Composable
private fun GameOverContent(score: Long, highScore: Long, matchStats: MatchStats, nbackLevel: Int) {
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
            MatchStatisticsSection(matchStats, nbackLevel)
        }
    }
}

@Composable
private fun MatchStatisticsSection(matchStats: MatchStats, nbackLevel: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$nbackLevel-Back Match Stats",
            color = Color.Cyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp)

        Spacer(modifier = Modifier.height(8.dp))

        // Match statistics
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

        // Non-match statistics
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