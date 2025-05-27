package io.github.chaosdave34.benzol

import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.english
import benzol.composeapp.generated.resources.german
import org.jetbrains.compose.resources.StringResource

enum class SupportedLanguage(val resource: StringResource, val locale: String) {
    GERMAN(Res.string.german, "de"),
    ENGLISH(Res.string.english, "en");

    companion object {
        fun fromLocale(locale: String) = entries.find { it.locale == locale }
    }
}