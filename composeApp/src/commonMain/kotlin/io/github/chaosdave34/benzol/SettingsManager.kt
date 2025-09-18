package io.github.chaosdave34.benzol

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Settings() {
    private val darkThemeKey = "dark_theme"
    private val languageKey = "language"
    private val disclaimerConfirmedKey = "disclaimer_confirmed"
    private val exportUrlKey = "export_url"

    var darkTheme by BooleanDelegate(darkThemeKey, false)
    var language by enumDelegate(languageKey, SupportedLanguage.German)
    var disclaimerConfirmed by BooleanDelegate(disclaimerConfirmedKey, false)
    var exportUrl by StringDelegate(exportUrlKey, "")

    class StringDelegate(val key: String, val default: String) : ReadWriteProperty<Settings, String> {
        override fun getValue(thisRef: Settings, property: KProperty<*>): String {
            return getSetting(key) ?: default
        }

        override fun setValue(thisRef: Settings, property: KProperty<*>, value: String) = setSetting(key, value)
    }

    class BooleanDelegate(val key: String, val default: Boolean) : ReadWriteProperty<Settings, Boolean> {
        override operator fun getValue(thisRef: Settings, property: KProperty<*>): Boolean {
            return getSetting(key)?.toBoolean() ?: default
        }

        override operator fun setValue(thisRef: Settings, property: KProperty<*>, value: Boolean) = setSetting(key, value.toString())
    }

    inline fun <reified T : Enum<T>> enumDelegate(key: String, default: T) = object : ReadWriteProperty<Settings, T> {
        override fun getValue(thisRef: Settings, property: KProperty<*>): T {
            val value = getSetting(key) ?: return default
            return try {
                enumValueOf(value)
            } catch (_: Exception) {
                default
            }
        }

        override fun setValue(thisRef: Settings, property: KProperty<*>, value: T) = setSetting(key, value.name)
    }
}

expect fun getSetting(key: String): String?

expect fun setSetting(key: String, value: String)