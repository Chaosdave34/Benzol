package io.github.chaosdave34.benzol.ui.adaptive

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

@Composable
fun AdaptivePageColumn(
    scrollState: ScrollState,
    maxWidth: Dp = WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .verticalScroll(scrollState)
            .widthIn(max = maxWidth)
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}
