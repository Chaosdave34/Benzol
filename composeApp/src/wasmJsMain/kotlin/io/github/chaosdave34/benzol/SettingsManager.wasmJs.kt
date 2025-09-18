package io.github.chaosdave34.benzol

import kotlinx.browser.window

actual fun getSetting(key: String) = window.localStorage.getItem(key)

actual fun setSetting(key: String, value: String) = window.localStorage.setItem(key, value)
