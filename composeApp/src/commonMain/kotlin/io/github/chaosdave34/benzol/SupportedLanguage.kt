package io.github.chaosdave34.benzol

import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.english
import benzol.composeapp.generated.resources.german
import io.github.chaosdave34.benzol.data.Labeled
import org.jetbrains.compose.resources.StringResource

enum class SupportedLanguage(override val label: StringResource, val locale: String) : Labeled {
    German(Res.string.german, "de"),
    English(Res.string.english, "en");
}