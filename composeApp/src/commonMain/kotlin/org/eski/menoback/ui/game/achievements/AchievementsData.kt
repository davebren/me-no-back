package org.eski.menoback.ui.game.achievements

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import org.eski.menoback.ui.game.data.GameStatsData
import org.eski.menoback.ui.game.model.MatchStats
import org.eski.menoback.ui.game.model.NbackStimulus

class AchievementsData(private val settings: Settings, val stats: GameStatsData) {
    companion object {
        private const val achievementsKey = "settings.achievements"
        private const val levelAchievementKey = "$achievementsKey.levelAchieved"

        fun levelAchievementKey(level: Int) = "$levelAchievementKey.$level"
    }

    fun processGameResults(
        score: Long,
        streak: Int,
        matchStats: MatchStats,
        nbackStimuli: List<NbackStimulus>
    ) {
        stats.incrementGamesPlayed()
    }

    fun levelAchieved(level: Int) = settings.getBoolean(levelAchievementKey(level), false)
    fun putLevelAchieved(level: Int) = settings.putBoolean(levelAchievementKey(level), true)
}