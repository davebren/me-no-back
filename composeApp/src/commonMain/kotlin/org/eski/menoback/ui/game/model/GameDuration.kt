package org.eski.menoback.ui.game.model

import org.eski.util.SettingsEnum

enum class GameDuration(
  override val stableId: Int,
  val durationSeconds: Int,
): SettingsEnum {
//  thirtySeconds(0, 30),
  oneMinute(1, 60),
  twoMinutes(2, 120),
  fiveMinutes(3, 300),
  tenMinutes(4, 600),
  thirtyMinutes(5, 1800);

  companion object {
    val default = fiveMinutes

    val secondsList = entries.map { it.durationSeconds }
  }
}