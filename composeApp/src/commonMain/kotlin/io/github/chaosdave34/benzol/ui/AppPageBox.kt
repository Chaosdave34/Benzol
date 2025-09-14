package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
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
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(16.dp)
            ) {
                Text(title, style = MaterialTheme.typography.headlineLarge)
            }
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