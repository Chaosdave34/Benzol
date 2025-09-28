package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import io.github.chaosdave34.benzol.LocalSnackbarHostState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

context(viewModel: SafetySheetViewModel)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppPageBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.(ScrollState) -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val uiState by viewModel.uiState.collectAsState()

    val scrollBehavior = if (windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)) {
        TopAppBarDefaults.pinnedScrollBehavior()
    } else {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    }

    val fabOrToolbarVisible = true

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(uiState.selectedDestination.label),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                titleHorizontalAlignment = Alignment.CenterHorizontally,
                subtitle = {},
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .fillMaxWidth(0.7f)
            )
        },
        floatingActionButton = {
            if (!windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ||
                !windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
            ) {
                FloatingActionButtonMenu(
                    visible = fabOrToolbarVisible,
                    onResetInput = {
                        scope.launch {
                            scope.launch { viewModel.resetInput() }
                        }
                    },
                    onChooseFile = viewModel::openFileChooser,
                    onSaveFile = viewModel::openFileSaver,
                    onExportPdf = viewModel::openPdfExport
                )
            }
        }
    ) { contentPadding ->
        Box(
            modifier.padding(contentPadding),
            contentAlignment = contentAlignment,
        ) {
            val scrollState = rememberScrollState()
            content(scrollState)
            CustomScrollbar(scrollState)

            if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) &&
                windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
            ) {
                Toolbar(
                    visible = fabOrToolbarVisible,
                    onResetInput = {
                        scope.launch { viewModel.resetInput() }
                    },
                    onChooseFile = viewModel::openFileChooser,
                    onSaveFile = viewModel::openFileSaver,
                    onExportPdf = viewModel::openPdfExport
                )
            }
        }
    }
}