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

enum class Destination(val route: String, val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    SHEET("sheet", "Datenblatt", Icons.AutoMirrored.Filled.Assignment, Icons.AutoMirrored.Outlined.Assignment),
    PREVIEW("preview", "Vorschau", Icons.Filled.Preview, Icons.Outlined.Preview),
    Settings("settings", "Einstellungen", Icons.Filled.Settings, Icons.Outlined.Settings),
    ABOUT("about", "Information", Icons.Filled.Info, Icons.Outlined.Info)
}