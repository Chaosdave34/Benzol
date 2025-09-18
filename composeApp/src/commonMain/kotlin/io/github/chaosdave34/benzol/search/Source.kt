package io.github.chaosdave34.benzol.search

import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.custom_source
import benzol.composeapp.generated.resources.gestis
import org.jetbrains.compose.resources.StringResource

enum class Source(val label: StringResource) {
    Custom(Res.string.custom_source),
    Gestis(Res.string.gestis)
}