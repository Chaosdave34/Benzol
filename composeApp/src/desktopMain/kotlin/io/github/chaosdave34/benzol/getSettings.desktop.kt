package io.github.chaosdave34.benzol

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

actual fun getSettings(): Settings = PreferencesSettings(Preferences.userRoot().node("io.github.chaosdave34.benzol"))