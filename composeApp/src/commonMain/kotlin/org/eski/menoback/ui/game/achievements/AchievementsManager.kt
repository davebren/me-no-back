package org.eski.menoback.ui.game.achievements

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import org.eski.menoback.ui.game.data.GameStatsData
import org.eski.menoback.ui.game.model.MatchStats
import org.eski.menoback.ui.game.model.NbackStimulus
import org.eski.util.safeJsonDecode
import org.eski.util.safeJsonEncode

class AchievementsManager(private val settings: Settings) {
    companion object {
        private const val ACHIEVEMENTS_KEY = "settings.achievements"
        private const val TOTAL_GAMES_PLAYED_KEY = "stats.games.total"
        
        // Achievement notification
        private const val MAX_NOTIFICATIONS = 5
        private const val RECENT_ACHIEVEMENTS_KEY = "achievements.recent"
    }
    
    private val _achievements = MutableStateFlow(
        getStoredAchievements() ?: AchievementCollection.createDefault()
    )
    val achievements: StateFlow<AchievementCollection> = _achievements.asStateFlow()
    
    private val _recentUnlocks = MutableStateFlow<List<Achievement>>(emptyList())
    val recentUnlocks: StateFlow<List<Achievement>> = _recentUnlocks.asStateFlow()
    
    init {
        loadRecentUnlocks()
    }
    
    fun getTotalGamesPlayed(): Int = settings.getInt(TOTAL_GAMES_PLAYED_KEY, 0)
    
    fun incrementGamesPlayed() {
        val currentCount = getTotalGamesPlayed()
        settings.putInt(TOTAL_GAMES_PLAYED_KEY, currentCount + 1)
        checkGameCountAchievements(currentCount + 1)
    }
    
    private fun loadRecentUnlocks() {
        val json = settings.getStringOrNull(RECENT_ACHIEVEMENTS_KEY)
        json?.let {
            val achievementIds = it.safeJsonDecode<List<String>>() ?: return
            _recentUnlocks.value = achievementIds
                .mapNotNull { id -> _achievements.value.getAchievementById(id) }
        }
    }
    
    private fun addRecentUnlock(achievement: Achievement) {
        val current = _recentUnlocks.value.toMutableList()
        current.add(0, achievement) // Add to the beginning
        
        // Limit to MAX_NOTIFICATIONS
        val updated = if (current.size > MAX_NOTIFICATIONS) {
            current.subList(0, MAX_NOTIFICATIONS)
        } else {
            current
        }
        
        _recentUnlocks.value = updated
        saveRecentUnlocks()
    }
    
    private fun saveRecentUnlocks() {
        val ids = _recentUnlocks.value.map { it.id }
        ids.safeJsonEncode()?.let {
            settings.putString(RECENT_ACHIEVEMENTS_KEY, it)
        }
    }
    
    fun clearRecentUnlocks() {
        _recentUnlocks.value = emptyList()
        settings.remove(RECENT_ACHIEVEMENTS_KEY)
    }
    
    private fun getStoredAchievements(): AchievementCollection? {
        val json = settings.getStringOrNull(ACHIEVEMENTS_KEY)
        return json?.safeJsonDecode<AchievementCollection>()
    }
    
    private fun saveAchievements() {
        _achievements.value.safeJsonEncode()?.let {
            settings.putString(ACHIEVEMENTS_KEY, it)
        }
    }
    
    fun checkLevelAchievements(level: Int) {
        val id = "level_$level"
        val achievement = _achievements.value.getAchievementById(id) ?: return
        
        if (!achievement.isLocked) return
        
        val updated = _achievements.value.withUnlockedAchievement(
            id, 
            Clock.System.now().toEpochMilliseconds()
        )
        
        _achievements.value = updated
        saveAchievements()
        addRecentUnlock(updated.getAchievementById(id)!!)
    }
    
    fun checkScoreAchievements(score: Long) {
        val scoreAchievements = _achievements.value.getAchievementsByType(AchievementType.HIGH_SCORE)
        
        for (achievement in scoreAchievements) {
            if (!achievement.isLocked && achievement.requiredValue <= score) continue
            
            if (achievement.requiredValue <= score) {
                val updated = _achievements.value.withUnlockedAchievement(
                    achievement.id,
                    Clock.System.now().toEpochMilliseconds()
                )
                _achievements.value = updated
                addRecentUnlock(updated.getAchievementById(achievement.id)!!)
            } else {
                // Update progress
                val progress = score.toFloat() / achievement.requiredValue.toFloat()
                val updated = _achievements.value.withProgressUpdate(achievement.id, progress)
                _achievements.value = updated
            }
        }
        
        saveAchievements()
    }
    
    fun checkStreakAchievements(streak: Int) {
        val streakAchievements = _achievements.value.getAchievementsByType(AchievementType.STREAK)
        
        for (achievement in streakAchievements) {
            if (!achievement.isLocked && achievement.requiredValue <= streak) continue
            
            if (achievement.requiredValue <= streak) {
                val updated = _achievements.value.withUnlockedAchievement(
                    achievement.id,
                    Clock.System.now().toEpochMilliseconds()
                )
                _achievements.value = updated
                addRecentUnlock(updated.getAchievementById(achievement.id)!!)
            } else {
                // Update progress
                val progress = streak.toFloat() / achievement.requiredValue.toFloat()
                val updated = _achievements.value.withProgressUpdate(achievement.id, progress)
                _achievements.value = updated
            }
        }
        
        saveAchievements()
    }
    
    fun checkAccuracyAchievements(matchStats: MatchStats) {
        val accuracy = matchStats.accuracyPercentage
        val accuracyAchievements = _achievements.value.getAchievementsByType(AchievementType.ACCURACY)
        
        for (achievement in accuracyAchievements) {
            if (!achievement.isLocked && achievement.requiredValue <= accuracy) continue
            
            if (achievement.requiredValue <= accuracy) {
                val updated = _achievements.value.withUnlockedAchievement(
                    achievement.id,
                    Clock.System.now().toEpochMilliseconds()
                )
                _achievements.value = updated
                addRecentUnlock(updated.getAchievementById(achievement.id)!!)
            }
        }
        
        // Check for perfect game achievement
        if (accuracy == 100f && matchStats.totalDecisions > 10) {
            val perfectGameId = "perfect_game"
            val perfectAchievement = _achievements.value.getAchievementById(perfectGameId)
            
            if (perfectAchievement != null && perfectAchievement.isLocked) {
                val updated = _achievements.value.withUnlockedAchievement(
                    perfectGameId,
                    Clock.System.now().toEpochMilliseconds()
                )
                _achievements.value = updated
                addRecentUnlock(updated.getAchievementById(perfectGameId)!!)
            }
        }
        
        saveAchievements()
    }
    
    fun checkGameCountAchievements(gameCount: Int) {
        val gameCountAchievements = _achievements.value.getAchievementsByType(AchievementType.GAME_COUNT)
        
        for (achievement in gameCountAchievements) {
            if (!achievement.isLocked && achievement.requiredValue <= gameCount) continue
            
            if (achievement.requiredValue <= gameCount) {
                val updated = _achievements.value.withUnlockedAchievement(
                    achievement.id,
                    Clock.System.now().toEpochMilliseconds()
                )
                _achievements.value = updated
                addRecentUnlock(updated.getAchievementById(achievement.id)!!)
            } else {
                // Update progress
                val progress = gameCount.toFloat() / achievement.requiredValue.toFloat()
                val updated = _achievements.value.withProgressUpdate(achievement.id, progress)
                _achievements.value = updated
            }
        }
        
        saveAchievements()
    }
    
    fun checkSpecialAchievements(nbackStimuli: List<NbackStimulus>) {
        // Check for dual n-back (both shape and color)
        if (nbackStimuli.size >= 2 && 
            nbackStimuli.any { it.type == NbackStimulus.Type.shape } && 
            nbackStimuli.any { it.type == NbackStimulus.Type.color }) {
            
            val dualNbackId = "both_nback"
            val dualAchievement = _achievements.value.getAchievementById(dualNbackId)
            
            if (dualAchievement != null && dualAchievement.isLocked) {
                val updated = _achievements.value.withUnlockedAchievement(
                    dualNbackId,
                    Clock.System.now().toEpochMilliseconds()
                )
                _achievements.value = updated
                addRecentUnlock(updated.getAchievementById(dualNbackId)!!)
                saveAchievements()
            }
        }
    }
    
    fun processGameResults(
        score: Long,
        streak: Int,
        matchStats: MatchStats,
        nbackStimuli: List<NbackStimulus>
    ) {
        incrementGamesPlayed()
        checkScoreAchievements(score)
        checkStreakAchievements(streak)
        checkAccuracyAchievements(matchStats)
        checkSpecialAchievements(nbackStimuli)
    }
}