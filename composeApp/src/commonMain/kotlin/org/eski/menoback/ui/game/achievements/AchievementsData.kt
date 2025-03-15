package org.eski.menoback.ui.game.achievements

import com.russhwolf.settings.Settings
import org.eski.menoback.ui.game.data.GameStatsData
import org.eski.menoback.ui.game.model.MatchStats
import org.eski.menoback.ui.game.model.NbackStimulus

class AchievementsData(private val settings: Settings, val stats: GameStatsData) {
    companion object {
        private const val achievementsKey = "settings.achievements"
        private const val levelAchievementKey = "$achievementsKey.levelAchieved"

        fun levelAchievementKey(level: Int, stimuliRequired: Int?) = "$levelAchievementKey.$level" +
            if (stimuliRequired != null) ".$stimuliRequired" else ""
    }

    fun processGameResults(
        score: Long,
        streak: Int,
        matchStats: MatchStats,
        nbackStimuli: List<NbackStimulus>
    ) {
        stats.incrementGamesPlayed()
    }

    fun levelAchieved(level: Int, stimuliRequired: Int? = null)
        = settings.getBoolean(levelAchievementKey(level, stimuliRequired), false)
    fun putLevelAchieved(level: Int, stimuliRequired: Int? = null)
        = settings.putBoolean(levelAchievementKey(level, stimuliRequired), true)
}