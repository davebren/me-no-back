package org.eski.menoback.data

import com.russhwolf.settings.Settings
import org.eski.menoback.ui.game.achievements.AchievementsData
import org.eski.menoback.ui.keybinding.KeyBindingSettings
import org.eski.menoback.ui.game.data.GameSettings
import org.eski.menoback.ui.game.data.GameStatsData
import org.eski.menoback.ui.game.data.IntroSettings

internal val settings = Settings()

internal val keyBindingSettings = KeyBindingSettings(settings)
internal val gameStatsData = GameStatsData(settings)
internal val gameSettings = GameSettings(settings, gameStatsData)
internal val introSettings = IntroSettings(settings)
internal val achievementsData = AchievementsData(settings, gameStatsData)
