package org.eski.menoback.ui.game.achievements

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.ui.graphics.vector.ImageVector


open class Achievement(
  open val title: String,
  open val description: String,
  open val unlocked: Boolean = true,
  open val progress: Float? = null, // 0.0 to 1.0 for progress-based achievements
  open val completedTimestamp: Long? = null,
  open val icon: ImageVector = Icons.Default.Psychology,
  open val secret: Boolean = false,
) {
//    fun createDefault(): AchievementCollection {
//      val achievements = mutableMapOf<String, Achievement>()
//
//      // High score achievements
//      val scoreThresholds = listOf(1000L, 5000L, 10000L, 25000L, 50000L, 100000L)
//      scoreThresholds.forEach { score ->
//        val id = "score_$score"
//        achievements[id] = Achievement(
//          id = id,
//          title = "Score Champion ${score / 1000}K",
//          description = "Achieve a score of $score points",
//          requiredValue = score,
//          unlocked = true
//        )
//      }
//
//      // Streak achievements
//      val streakThresholds = listOf(10, 25, 50, 100)
//      streakThresholds.forEach { streak ->
//        val id = "streak_$streak"
//        achievements[id] = Achievement(
//          id = id,
//          title = "$streak Streak Master",
//          description = "Achieve a streak of $streak correct matches",
//          requiredValue = streak.toLong(),
//          unlocked = true
//        )
//      }
//
//      // Accuracy achievements
//      val accuracyThresholds = listOf(75, 85, 95, 99)
//      accuracyThresholds.forEach { accuracy ->
//        val id = "accuracy_$accuracy"
//        achievements[id] = Achievement(
//          id = id,
//          title = "$accuracy% Precision",
//          description = "Complete a game with $accuracy% accuracy",
//          requiredValue = accuracy.toLong(),
//          unlocked = true
//        )
//      }
//
//      // Game count achievements
//      val gameCountThresholds = listOf(10, 50, 100, 500, 1000)
//      gameCountThresholds.forEach { count ->
//        val id = "games_$count"
//        achievements[id] = Achievement(
//          id = id,
//          title = "Dedicated Player",
//          description = "Complete $count games",
//          requiredValue = count.toLong(),
//          unlocked = true
//        )
//      }
//
//      // Special achievements
//      achievements["both_nback"] = Achievement(
//        id = "both_nback",
//        title = "Dual Processor",
//        description = "Complete a game using both shape and color N-back simultaneously",
//        unlocked = true
//      )
//
//      achievements["perfect_game"] = Achievement(
//        id = "perfect_game",
//        title = "Perfect Recall",
//        description = "Complete a game with 100% accuracy",
//        unlocked = true
//      )
//
//      return AchievementCollection(achievements)
//    }
//  }
}

enum class AchievementType {
  LEVEL_PROGRESSION,
  HIGH_SCORE,
  ACCURACY,
  SPECIAL
}

/**
 * Represents a collection of achievements in the game
 */
data class AchievementCollection(
  val achievementMap: Map<String, Achievement> = emptyMap(),
) {

  fun getAllAchievements(): List<Achievement> = achievementMap.values.toList()
}