package io.github.chaosdave34.benzol

import java.util.*

actual fun setLanguage(language: SupportedLanguage) {
    Locale.setDefault(Locale.of(language.locale))
}