package io.github.chaosdave34.benzol.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Scrollbar(scrollState: ScrollState) {
    VerticalScrollbar(
        modifier = Modifier.fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState),
        style = defaultScrollbarStyle().copy(
            hoverColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unhoverColor = MaterialTheme.colorScheme.surfaceContainer
        )
    )
}