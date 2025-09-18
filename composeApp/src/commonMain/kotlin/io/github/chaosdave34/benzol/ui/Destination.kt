package io.github.chaosdave34.benzol.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import benzol.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

enum class Destination(val route: String, val label: StringResource, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    Sheet("sheet", Res.string.sheet, Icons.AutoMirrored.Filled.Assignment, Icons.AutoMirrored.Outlined.Assignment),
    Preview("preview", Res.string.preview, Icons.Filled.Preview, Icons.Outlined.Preview),
    Settings("settings", Res.string.settings, Icons.Filled.Settings, Icons.Outlined.Settings),
    About("about", Res.string.about, Icons.Filled.Info, Icons.Outlined.Info)
}