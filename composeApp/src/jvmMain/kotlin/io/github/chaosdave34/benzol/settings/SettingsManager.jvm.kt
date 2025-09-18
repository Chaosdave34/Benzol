package io.github.chaosdave34.benzol.settings

import java.util.prefs.Preferences

private val preferences: Preferences = Preferences.userRoot().node("io.github.chaosdave34.benzol")

actual fun getSetting(key: String): String? {
    return if (preferences.keys().contains(key)) preferences.get(key, "") else null
}

actual fun setSetting(key: String, value: String) {
    preferences.put(key, value)
}