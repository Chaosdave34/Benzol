package io.github.chaosdave34.benzol

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings

actual fun getSettings(): Settings = StorageSettings()