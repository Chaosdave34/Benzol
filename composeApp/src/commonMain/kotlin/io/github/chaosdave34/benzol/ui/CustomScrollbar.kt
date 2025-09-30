package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.CustomScrollbar(
    scrollbarAdapter: ScrollbarAdapter,
    offset: Dp = 0.dp
) {
    VerticalScrollbar(
        modifier = Modifier
            .fillMaxHeight()
            .align(Alignment.TopEnd)
            .offset(x = offset),
        adapter = scrollbarAdapter,
        style = defaultScrollbarStyle().copy(
            unhoverColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            hoverColor = MaterialTheme.colorScheme.secondaryContainer,
            thickness = 6.dp
        )
    )
}