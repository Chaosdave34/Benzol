package io.github.chaosdave34.benzol.settings

import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.dark_theme
import benzol.composeapp.generated.resources.follow_system
import benzol.composeapp.generated.resources.light_theme
import org.jetbrains.compose.resources.StringResource

enum class Theme(val label: StringResource) {
    System(Res.string.follow_system),
    Light(Res.string.light_theme),
    Dark(Res.string.dark_theme)
}