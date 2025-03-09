package org.eski.menoback.ui.game.data

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.eski.menoback.data.settings

private const val INTRO_SHOWN_KEY = "settings.intro.shown"

class IntroSettings(private val settings: Settings) {
    private val _introShown = MutableStateFlow(settings.getBoolean(INTRO_SHOWN_KEY, false))
    val introShown = _introShown.asStateFlow()
    
    fun setIntroShown(shown: Boolean) {
        _introShown.value = shown
        settings.putBoolean(INTRO_SHOWN_KEY, shown)
    }
}

// Initialize with the application settings
val introSettings = IntroSettings(settings)