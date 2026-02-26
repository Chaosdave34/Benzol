package io.github.chaosdave34.benzol.ui


import benzol.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class Destination(val route: String, val label: StringResource, val selectedIcon: DrawableResource, val unselectedIcon: DrawableResource) {
    Sheet("sheet", Res.string.sheet, Res.drawable.assignment_filled, Res.drawable.assignment),
    Preview("preview", Res.string.preview, Res.drawable.preview_filled, Res.drawable.preview),
    Settings("settings", Res.string.settings, Res.drawable.settings_filled, Res.drawable.settings),
    About("about", Res.string.about, Res.drawable.info_filled, Res.drawable.info)
}