package org.eski.menoback.ui.game.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import org.eski.menoback.ui.game.model.NbackStimulus

class GameStatsData(val settings: Settings) {
    companion object {
        private const val statsKey = "stats.game"
        
        private fun highScoreKey(durationSeconds: Int, nback: List<NbackStimulus>)
            = "${statsKey}.highscore.$durationSeconds.${nback.joinToString(".")}"
    }

    val lastScoreUpdate = MutableStateFlow<HighScoreUpdate?>(null)
    
    fun highScore(durationSeconds: Int, nback: List<NbackStimulus>)
        = settings.getLong(highScoreKey(durationSeconds, nback), 0)
    fun putHighScore(score: Long, durationSeconds: Int, nback: List<NbackStimulus>) {
        settings.putLong(highScoreKey(durationSeconds, nback), score)
        lastScoreUpdate.value = HighScoreUpdate(durationSeconds, nback, score)
    }

    data class HighScoreUpdate(val durationSeconds: Int, val nback: List<NbackStimulus>, val score: Long)
}