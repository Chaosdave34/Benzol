package io.github.chaosdave34.benzol.ui.adaptive

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowSizeClass
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource

@Composable
fun AdaptiveDialog(
    title: String,
    onDismissRequest: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val fullscreen = !adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = !fullscreen)
    ) {
        if (fullscreen) {
            Surface {
                AdaptiveDialogScaffold(
                    title = title,
                    fullscreen = fullscreen,
                    onDismissRequest = onDismissRequest,
                    actions = actions,
                    content = content
                )
            }
        } else {
            Card(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
            ) {
                AdaptiveDialogScaffold(
                    title = title,
                    fullscreen = fullscreen,
                    onDismissRequest = onDismissRequest,
                    actions = actions,
                    content = content
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AdaptiveDialogScaffold(
    title: String,
    fullscreen: Boolean,
    onDismissRequest: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        Modifier
            .fillMaxWidth()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    if (fullscreen) {
                        IconButton(
                            onClick = onDismissRequest
                        ) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(Res.string.cancel))
                        }
                    }
                },
                actions = {
                    if (fullscreen) {
                        actions()
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            if (!fullscreen) {
                FlexibleBottomAppBar(
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    actions()
                }
            }
        }
    ) { contentPadding ->
        Box(
            Modifier
                .padding(contentPadding)
                .padding(horizontal = 12.dp)
        ) {
            content()
        }
    }
}


