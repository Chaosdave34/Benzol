package io.github.chaosdave34.benzol.ui.adaptive

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.window.core.layout.WindowSizeClass

@Composable
fun AdaptiveButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()

    if (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)) {
        Button(
            onClick = onClick,
            enabled = enabled,
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        FilledIconButton(
            onClick = onClick,
            enabled = enabled,
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }
}