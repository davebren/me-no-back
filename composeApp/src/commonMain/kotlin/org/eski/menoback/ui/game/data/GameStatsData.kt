package org.eski.menoback.ui.game.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow

class GameStatsData(val settings: Settings) {
    companion object {
        private const val statsKey = "stats.game"
        
        // Key pattern for storing high scores: "stats.game.highscore.{duration}"
        private fun highScoreKey(duration: Int) = "$statsKey.highscore.$duration"
        
        // Key pattern for storing n-back level: "stats.game.level.{duration}"
        private fun nBackLevelKey(duration: Int) = "$statsKey.level.$duration"
    }
    
    // Map to store high scores for each game duration with default value 0
    val highScores = GameSettings.AVAILABLE_DURATIONS.associateWith { duration ->
        MutableStateFlow(settings.getInt(highScoreKey(duration), 0))
    }.toMutableMap()
    
    // Map to store n-back levels for each game duration with default value 1
    val nbackLevels = GameSettings.AVAILABLE_DURATIONS.associateWith { duration ->
        MutableStateFlow(settings.getInt(nBackLevelKey(duration), 1))
    }.toMutableMap()
    
    // Update high score if current score is higher
    fun updateHighScore(duration: Int, score: Int, nBackLevel: Int) {
        val currentHighScore = highScores[duration]?.value ?: 0
        
        if (score > currentHighScore) {
            highScores[duration]?.value = score
            nbackLevels[duration]?.value = nBackLevel
            
            settings.putInt(highScoreKey(duration), score)
            settings.putInt(nBackLevelKey(duration), nBackLevel)
        }
    }
    
    // Reset all high scores and n-back levels
    fun resetAllStats() {
        GameSettings.AVAILABLE_DURATIONS.forEach { duration ->
            highScores[duration]?.value = 0
            nbackLevels[duration]?.value = 1
            
            settings.putInt(highScoreKey(duration), 0)
            settings.putInt(nBackLevelKey(duration), 1)
        }
    }
    
    // Format high score display text
    fun formatHighScoreText(duration: Int): String {
        val highScore = highScores[duration]?.value ?: 0
        val nBackLevel = nbackLevels[duration]?.value ?: 1
        
        return if (highScore > 0) {
            "Best: $highScore (${nBackLevel}-back)"
        } else {
            "No high score yet"
        }
    }
}