package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import io.github.chaosdave34.benzol.settings.Theme
import io.github.chaosdave34.benzol.ui.about.AboutPage
import io.github.chaosdave34.benzol.ui.preview.PreviewPage
import io.github.chaosdave34.benzol.ui.safetysheet.SafetySheetPage
import io.github.chaosdave34.benzol.ui.settings.SettingsPage
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Layout(
    viewModel: SafetySheetViewModel
) {
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()

    val adaptiveInfo = currentWindowAdaptiveInfo()

    val navController = rememberNavController()
    val startDestination = Destination.Sheet

    var fabOrToolbarVisible by rememberSaveable { mutableStateOf(true) }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        fabOrToolbarVisible = destination.route in listOf(Destination.Sheet, Destination.Preview).map { it.route }
    }

    val darkTheme = when (uiState.theme) {
        Theme.System -> isSystemInDarkTheme()
        Theme.Light -> false
        Theme.Dark -> true
    }

    MaterialExpressiveTheme(
        colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
    ) {
        DisclaimerDialog(
            !uiState.disclaimerConfirmed,
            viewModel::confirmDisclaimer
        )
        var selectedDestination by rememberSaveable { mutableStateOf(startDestination) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(selectedDestination.label), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    titleHorizontalAlignment = Alignment.CenterHorizontally,
                    subtitle = {}
                )

            },
            snackbarHost = {
                SnackbarHost(
                    hostState = viewModel.snackbarHostState.value,
                    modifier = Modifier
                        .padding(bottom = 60.dp)
                        .fillMaxWidth(0.7f)
                )
            },
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            }
        ) { contentPadding ->
            Box(Modifier.fillMaxSize().padding(contentPadding)) {
                val navigationScaffoldState = rememberNavigationSuiteScaffoldState()

                NavigationSuiteScaffold(
                    state = navigationScaffoldState,
                    navigationItems = {
                        Destination.entries.forEach { destination ->
                            val selected = selectedDestination == destination
                            NavigationSuiteItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(route = destination.route)
                                    selectedDestination = destination
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = stringResource(destination.label)
                                    )
                                },
                                label = { Text(stringResource(destination.label)) },
                            )
                        }
                    },
                    primaryActionContent = {
                        if (!adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                            FloatingActionButtonMenu(
                                visible = fabOrToolbarVisible,
                                viewModel = viewModel
                            )
                        }
                    }
                ) {
                    Box(Modifier.fillMaxSize()) {
                        AppNavHost(
                            navController = navController,
                            startDestination = startDestination,
                            viewModel = viewModel
                        )

                        if (adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                            Toolbar(
                                visible = fabOrToolbarVisible,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    viewModel: SafetySheetViewModel
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.Sheet -> SafetySheetPage(viewModel)
                    Destination.Preview -> PreviewPage(viewModel)
                    Destination.Settings -> SettingsPage(viewModel)
                    Destination.About -> AboutPage()
                }
            }
        }
    }
}