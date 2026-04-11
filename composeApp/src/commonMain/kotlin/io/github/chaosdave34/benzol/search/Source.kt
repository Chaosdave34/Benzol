package io.github.chaosdave34.benzol.search

import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.custom_source
import benzol.composeapp.generated.resources.gestis
import io.github.chaosdave34.benzol.data.Labeled
import org.jetbrains.compose.resources.StringResource

enum class Source(override val label: StringResource) : Labeled {
    Custom(Res.string.custom_source),
    Gestis(Res.string.gestis)
}