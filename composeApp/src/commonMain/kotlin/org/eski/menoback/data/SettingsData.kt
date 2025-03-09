package org.eski.menoback.data

import com.russhwolf.settings.Settings
import org.eski.menoback.ui.keybinding.KeyBindingSettings
import org.eski.menoback.ui.game.data.GameSettings
import org.eski.menoback.ui.game.data.GameStatsData
import org.eski.menoback.ui.game.data.IntroSettings
import org.eski.menoback.ui.game.data.NbackProgressData

internal val settings = Settings()

internal val keyBindingSettings = KeyBindingSettings(settings)
internal val nbackProgressData = NbackProgressData(settings)
internal val gameSettings = GameSettings(settings, nbackProgressData)
internal val gameStatsData = GameStatsData(settings)
internal val introSettings = IntroSettings(settings)
