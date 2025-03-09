package org.eski.menoback.ui.game.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import org.eski.menoback.ui.game.model.NbackStimulus
import org.eski.util.safeJsonDecode
import org.eski.util.safeJsonEncode

/**
 * Tracks the user's level progression in the n-back game
 * Users must achieve a minimum accuracy threshold at a given level
 * before being allowed to progress to the next level
 */
class NbackProgressData(private val settings: Settings) {
    companion object {
        private const val settingsKey = "settings.nback.progression"
        private const val progressionKey = "$settingsKey.data"
        const val accuracyThreshold = 85f // 85% accuracy required to unlock next level
    }

    // Map of level progressions by duration and stimulus type
    private val _progressions = MutableStateFlow<Map<LevelProgressionKey, LevelProgression>>(mapOf())
    val progressions = _progressions.asStateFlow()

    init {
        loadProgressions()
    }

    private fun loadProgressions() {
        val savedData = settings.getStringOrNull(progressionKey)
        savedData?.let { jsonString ->
            val decodedProgressions = jsonString.safeJsonDecode<Map<LevelProgressionKey, LevelProgression>>()
            decodedProgressions?.let { _progressions.value = it }
        }
    }

    private fun saveProgressions() {
        _progressions.value.safeJsonEncode()?.let { settings.putString(progressionKey, it) }
    }

    /**
     * Returns the highest unlocked level for the given duration and stimulus types
     */
    fun getMaxUnlockedLevel(durationSeconds: Int, stimuli: List<NbackStimulus>): Int {
        val key = LevelProgressionKey(durationSeconds, stimuli.map { it.type })
        return _progressions.value[key]?.maxUnlockedLevel ?: 2 // Default to level 2 if no progress
    }

    /**
     * Checks if a specific n-back level is unlocked for the given settings
     */
    fun isLevelUnlocked(level: Int, durationSeconds: Int, stimuli: List<NbackStimulus>): Boolean {
        if (level <= 2) return true // Level 2 is always unlocked
        
        val maxLevel = getMaxUnlockedLevel(durationSeconds, stimuli)
        return level <= maxLevel
    }

    /**
     * Updates progression after a game session completes
     * @return true if a new level was unlocked
     */
    fun updateProgress(
        level: Int,
        accuracy: Float,
        durationSeconds: Int,
        stimuli: List<NbackStimulus>
    ): Boolean {
        val key = LevelProgressionKey(durationSeconds, stimuli.map { it.type })
        val currentProgression = _progressions.value[key] ?: LevelProgression(2)
        val newLevelUnlocked = false
        
        // Only update if playing at the highest unlocked level and accuracy meets threshold
        if (level == currentProgression.maxUnlockedLevel && accuracy >= accuracyThreshold) {
            val updatedProgression = currentProgression.copy(maxUnlockedLevel = level + 1)
            _progressions.value = _progressions.value.toMutableMap().apply {
                put(key, updatedProgression)
            }
            saveProgressions()
            return true
        }
        
        return false
    }

    /**
     * Key that uniquely identifies progression for a specific game configuration
     */
    @Serializable
    data class LevelProgressionKey(
        val durationSeconds: Int,
        val stimulusTypes: List<NbackStimulus.Type>
    )

    /**
     * Tracks level progression for a specific game configuration
     */
    @Serializable
    data class LevelProgression(
        val maxUnlockedLevel: Int = 2, // Start with level 2 unlocked
        val bestAccuracy: Map<Int, Float> = mapOf() // Best accuracy achieved at each level
    )
}