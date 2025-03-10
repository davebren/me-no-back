package org.eski.menoback.ui.game.achievements

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.ui.graphics.vector.ImageVector
import org.eski.menoback.ui.game.model.NbackStimulus

data class LevelAchievement(
  val requiredStimuli: List<NbackStimulus>,
  val requiredLevel: Int,
  override val title: String,
  override val description: String,
  override val unlocked: Boolean = true,
  override val completedTimestamp: Long? = null,
  override val icon: ImageVector = Icons.Default.Psychology,
  override val secret: Boolean = false,
): Achievement(
  title = title,
  description = description,
  unlocked = unlocked,
  progress = null,
  completedTimestamp = completedTimestamp,
  icon = icon,
  secret = secret
) {

}
