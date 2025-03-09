package org.eski.menoback.ui.game.achievements

import kotlinx.serialization.Serializable
import org.eski.menoback.ui.game.model.NbackStimulus

@Serializable
data class Achievement(
    val id: String,
    val type: AchievementType,
    val title: String,
    val description: String,
    val iconName: String,
    val requiredValue: Long = 0,
    val isLocked: Boolean = true,
    val progress: Float = 0f, // 0.0 to 1.0 for progress-based achievements
    val completedTimestamp: Long? = null
)

enum class AchievementType {
    LEVEL_PROGRESSION,
    HIGH_SCORE,
    STREAK,
    ACCURACY,
    GAME_COUNT,
    SPECIAL
}

/**
 * Represents a collection of achievements in the game
 */
@Serializable
data class AchievementCollection(
    val achievementMap: Map<String, Achievement> = emptyMap()
) {
    fun getAchievementById(id: String): Achievement? = achievementMap[id]
    
    fun withUnlockedAchievement(id: String, completedTimestamp: Long): AchievementCollection {
        val achievement = achievementMap[id] ?: return this
        val updated = achievement.copy(isLocked = false, progress = 1f, completedTimestamp = completedTimestamp)
        return this.copy(achievementMap = achievementMap + (id to updated))
    }
    
    fun withProgressUpdate(id: String, newProgress: Float): AchievementCollection {
        val achievement = achievementMap[id] ?: return this
        val updated = achievement.copy(progress = newProgress)
        return this.copy(achievementMap = achievementMap + (id to updated))
    }
    
    fun getAchievementsByType(type: AchievementType): List<Achievement> {
        return achievementMap.values.filter { it.type == type }
    }
    
    fun getAllAchievements(): List<Achievement> = achievementMap.values.toList()
    
    companion object {
        fun createDefault(): AchievementCollection {
            val achievements = mutableMapOf<String, Achievement>()
            
            // Level progression achievements
            for (level in 3..10) {
                achievements["level_$level"] = Achievement(
                    id = "level_$level",
                    type = AchievementType.LEVEL_PROGRESSION,
                    title = "N-Back Master Level $level",
                    description = "Unlock $level-Back difficulty level",
                    iconName = "level_${level}_icon",
                    requiredValue = level.toLong(),
                    isLocked = true
                )
            }
            
            // High score achievements
            val scoreThresholds = listOf(1000L, 5000L, 10000L, 25000L, 50000L, 100000L)
            scoreThresholds.forEach { score ->
                val id = "score_$score"
                achievements[id] = Achievement(
                    id = id,
                    type = AchievementType.HIGH_SCORE,
                    title = "Score Champion ${score/1000}K",
                    description = "Achieve a score of $score points",
                    iconName = "trophy_icon",
                    requiredValue = score,
                    isLocked = true
                )
            }
            
            // Streak achievements
            val streakThresholds = listOf(10, 25, 50, 100)
            streakThresholds.forEach { streak ->
                val id = "streak_$streak"
                achievements[id] = Achievement(
                    id = id,
                    type = AchievementType.STREAK,
                    title = "$streak Streak Master",
                    description = "Achieve a streak of $streak correct matches",
                    iconName = "streak_icon",
                    requiredValue = streak.toLong(),
                    isLocked = true
                )
            }
            
            // Accuracy achievements
            val accuracyThresholds = listOf(75, 85, 95, 99)
            accuracyThresholds.forEach { accuracy ->
                val id = "accuracy_$accuracy"
                achievements[id] = Achievement(
                    id = id,
                    type = AchievementType.ACCURACY,
                    title = "$accuracy% Precision",
                    description = "Complete a game with $accuracy% accuracy",
                    iconName = "accuracy_icon",
                    requiredValue = accuracy.toLong(),
                    isLocked = true
                )
            }
            
            // Game count achievements
            val gameCountThresholds = listOf(10, 50, 100, 500, 1000)
            gameCountThresholds.forEach { count ->
                val id = "games_$count"
                achievements[id] = Achievement(
                    id = id,
                    type = AchievementType.GAME_COUNT,
                    title = "Dedicated Player",
                    description = "Complete $count games",
                    iconName = "games_icon",
                    requiredValue = count.toLong(),
                    isLocked = true
                )
            }
            
            // Special achievements
            achievements["both_nback"] = Achievement(
                id = "both_nback",
                type = AchievementType.SPECIAL,
                title = "Dual Processor",
                description = "Complete a game using both shape and color N-back simultaneously",
                iconName = "dual_icon",
                isLocked = true
            )
            
            achievements["perfect_game"] = Achievement(
                id = "perfect_game",
                type = AchievementType.SPECIAL,
                title = "Perfect Recall",
                description = "Complete a game with 100% accuracy",
                iconName = "perfect_icon",
                isLocked = true
            )
            
            return AchievementCollection(achievements)
        }
    }
}