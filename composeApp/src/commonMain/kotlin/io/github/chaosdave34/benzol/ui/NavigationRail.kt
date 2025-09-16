package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.collapse_rail
import benzol.composeapp.generated.resources.expand_rail
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NavigationRail(
    navController: NavHostController,
    startDestination: Destination,
    padding: PaddingValues
) {
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
    val state = rememberWideNavigationRailState()
    val scope = rememberCoroutineScope()

    val isCollapsed by remember { derivedStateOf { state.targetValue == WideNavigationRailValue.Collapsed } }
    val headerDescription = remember(isCollapsed) { if (!isCollapsed) Res.string.collapse_rail else Res.string.expand_rail }

    WideNavigationRail(
        Modifier.padding(padding),
        colors = WideNavigationRailDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        state = state,
        header = {
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                tooltip = { PlainTooltip { Text(stringResource(headerDescription)) } },
                state = rememberTooltipState()
            ) {
                IconButton(
                    modifier = Modifier.padding(start = 24.dp),
                    onClick = {
                        scope.launch {
                            if (isCollapsed) state.expand() else state.collapse()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isCollapsed) Icons.Filled.Menu else Icons.AutoMirrored.Filled.MenuOpen,
                        contentDescription = stringResource(headerDescription)
                    )
                }
            }
        }
    ) {
        Destination.entries.forEachIndexed { index, destination ->
            val selected = selectedDestination == index
            WideNavigationRailItem(
                selected = selected,
                onClick = {
                    navController.navigate(route = destination.route)
                    selectedDestination = index
                },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = stringResource(destination.label)
                    )
                },
                label = { Text(stringResource(destination.label)) },
                railExpanded = !isCollapsed
            )
        }
    }
}
