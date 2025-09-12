package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppPageBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    title: String,
    content: @Composable BoxScope.(ScrollState) -> Unit
) {
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(title)
                },
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            )
        },
    ) { contentPadding ->
        Box(
            modifier
                .padding(24.dp)
                .padding(contentPadding),
            contentAlignment = contentAlignment,
        ) {
            val scrollState = rememberScrollState()
            content(scrollState)
            CustomScrollbar(scrollState)
        }
    }
}