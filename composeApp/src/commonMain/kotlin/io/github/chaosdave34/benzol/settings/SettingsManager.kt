package io.github.chaosdave34.benzol.settings

expect fun getSetting(key: String): String?

expect fun setSetting(key: String, value: String)