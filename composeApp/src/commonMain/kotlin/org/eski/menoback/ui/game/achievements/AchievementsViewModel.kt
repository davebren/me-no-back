package org.eski.menoback.ui.game.achievements

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.eski.menoback.ui.game.achievements.AchievementType.*
import org.eski.menoback.ui.game.data.GameStatsData
import org.eski.menoback.ui.game.model.NbackStimulus

class AchievementsViewModel(val scope: CoroutineScope, val statsData: GameStatsData, val data: AchievementsData) {
  val levelAchievements = MutableStateFlow<List<LevelAchievement>>(levelAchievements())
  val scoreAchievements = MutableStateFlow<List<Achievement>>(emptyList())
  val accuracyAchievements = MutableStateFlow<List<Achievement>>(emptyList())
  val specialAchievements = MutableStateFlow<List<Achievement>>(emptyList())

  val allAchievements: StateFlow<List<Achievement>> = combine(levelAchievements, scoreAchievements) {
    levelAchievements, scoreAchievements ->

    levelAchievements + scoreAchievements
  }.stateIn(scope, SharingStarted.WhileSubscribed(), emptyList<Achievement>())

  val totalAchievementCount = allAchievements.map { it.size }
    .stateIn(scope, SharingStarted.WhileSubscribed(), 0)
  val unlockedAchievementCount = allAchievements.map { it.count { achievement -> achievement.unlocked } }
    .stateIn(scope, SharingStarted.WhileSubscribed(), 0)

  val tabs = MutableStateFlow<List<AchievementType>>(AchievementType.entries.toList())
  val typeDisplayed = MutableStateFlow<AchievementType>(AchievementType.entries.first())
  val displayedAchievements = combine(typeDisplayed, levelAchievements, scoreAchievements, accuracyAchievements, specialAchievements) {
    type, levels, scores, accuracies, specials ->
    when(type) {
      LEVEL_PROGRESSION -> levels
      HIGH_SCORE -> scores
      ACCURACY -> accuracies
      SPECIAL -> specials
    }
  }.stateIn(scope, SharingStarted.WhileSubscribed(), emptyList())

  fun typeClicked(type: AchievementType) { typeDisplayed.value = type }

  private fun levelAchievements(): List<LevelAchievement> {
    val achievements = mutableListOf<LevelAchievement>()

    val singleLevelAchievements = listOf(3, 4, 5, 6, 7, 8, 9, 10)
    singleLevelAchievements.forEach { level ->
      achievements.add(
        LevelAchievement(
          requiredStimuli = emptyList(),
          requiredStimuliCount = null,
          requiredLevel = level,
          title = "N-back Master Level $level",
          description = "Unlock the $level-Back level for any game mode",
          unlocked = data.levelAchieved(level),
        )
      )
    }
    val dualLevelAchievements = listOf(3, 4, 5, 6)
    dualLevelAchievements.forEach { level ->
      achievements.add(LevelAchievement(
        requiredStimuli = emptyList(),
        requiredStimuliCount = 2,
        requiredLevel = level,
        title = "Dual N-back Master Level $level",
        description = "Unlock the $level-Back level with two n-back stimuli enabled.",
        unlocked = data.levelAchieved(level, 2)
      ))
    }
    return achievements
  }

  fun checkAchievements(
    nextLevelUnlocked: Boolean,
    nbackSetting: List<NbackStimulus>,
    ){
    checkLevelAchievements(nextLevelUnlocked, nbackSetting)
  }

  private fun checkLevelAchievements(nextLevelUnlocked: Boolean, nbackSetting: List<NbackStimulus>) {
    if (!nextLevelUnlocked) return

    levelAchievements.value = levelAchievements.value.map { achievement ->
      if (!achievement.unlocked && nbackSetting.first().level == (achievement.requiredLevel - 1)
        && nbackSetting.containsAll(achievement.requiredStimuli)
        && nbackSetting.size >= (achievement.requiredStimuliCount ?: 1)) {

        data.putLevelAchieved(achievement.requiredLevel, achievement.requiredStimuliCount)
        achievement.copy(unlocked = true)
      } else achievement
    }
    levelAchievements
  }
}