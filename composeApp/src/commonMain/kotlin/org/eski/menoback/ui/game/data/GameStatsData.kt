package org.eski.menoback.ui.game.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import org.eski.menoback.ui.game.model.NbackStimulus

class GameStatsData(val settings: Settings) {
    companion object {
        private const val statsKey = "stats.game"
        private const val unlockedLevelKey = "settings.nback.unlockedLevel"
        const val accuracyThreshold = 85f
        const val defaultLevelUnlocked = 2
        
        private fun highScoreKey(durationSeconds: Int, nback: List<NbackStimulus>): String {
            val stimulusKeys = nback.sortedBy { it.type.stableId }.map { it.settingsKey() }
            return "${statsKey}.highscore.$durationSeconds.${stimulusKeys.joinToString(".")}"
        }

        private fun unlockedLevelKey(durationSeconds: Int, nback: List<NbackStimulus>): String {
            val stimulusKeys = nback.sortedBy { it.type.stableId }.map { "type-${it.type.stableId}" }
            return "${unlockedLevelKey}.$durationSeconds.${stimulusKeys.joinToString(separator = ".")}"
        }
    }

    val lastScoreUpdate = MutableStateFlow<HighScoreUpdate?>(null)
    val lastUnlockedLevelUpdate = MutableStateFlow<LastUnlockedLevelUpdate?>(null)
    
    fun highScore(durationSeconds: Int, nback: List<NbackStimulus>)
        = settings.getLong(highScoreKey(durationSeconds, nback), 0)
    fun putHighScore(score: Long, durationSeconds: Int, nback: List<NbackStimulus>) {
        settings.putLong(highScoreKey(durationSeconds, nback), score)
        lastScoreUpdate.value = HighScoreUpdate(durationSeconds, nback, score)
    }

    fun unlockedLevel(durationSeconds: Int, nback: List<NbackStimulus>)
        = settings.getInt(unlockedLevelKey(durationSeconds, nback), defaultLevelUnlocked)
    fun putUnlockedLevel(level: Int, durationSeconds: Int, nback: List<NbackStimulus>) {
        settings.putInt(unlockedLevelKey(durationSeconds, nback), level)
        lastUnlockedLevelUpdate.value = LastUnlockedLevelUpdate(durationSeconds, level, nback.map { it.type })
    }

    data class HighScoreUpdate(val durationSeconds: Int, val nback: List<NbackStimulus>, val score: Long)
    data class LastUnlockedLevelUpdate(val durationSeconds: Int, val level: Int, val nback: List<NbackStimulus.Type>)
}