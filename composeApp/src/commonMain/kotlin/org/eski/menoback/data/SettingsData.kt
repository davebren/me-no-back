package org.eski.menoback.data

import com.russhwolf.settings.Settings
import org.eski.menoback.ui.keybinding.KeyBindingSettings
import org.eski.menoback.ui.game.data.GameSettings
import org.eski.menoback.ui.game.data.GameStatsData

internal val settings = Settings()

internal val keyBindingSettings = KeyBindingSettings(settings)
internal val gameSettings = GameSettings(settings)
internal val gameStatsData = GameStatsData(settings)
