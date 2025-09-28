package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.chaosdave34.benzol.ui.about.AboutPage
import io.github.chaosdave34.benzol.ui.preview.PreviewPage
import io.github.chaosdave34.benzol.ui.safetysheet.SafetySheetPage
import io.github.chaosdave34.benzol.ui.settings.SettingsPage
import org.jetbrains.compose.resources.stringResource

context(viewModel: SafetySheetViewModel)
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Layout() {
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()

    val navController = rememberNavController()
    val navigationScaffoldState = rememberNavigationSuiteScaffoldState()

//    var fabOrToolbarVisible by rememberSaveable { mutableStateOf(true) }
//    navController.addOnDestinationChangedListener { _, destination, _ ->
//        fabOrToolbarVisible = destination.route in listOf(Destination.Sheet, Destination.Preview).map { it.route }
//    }

    NavigationSuiteScaffold(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            },
        state = navigationScaffoldState,
        navigationItems = {
            Destination.entries.forEach { destination ->
                val selected = uiState.selectedDestination == destination
                NavigationSuiteItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(route = destination.route)
                        viewModel.setSelectedDestination(destination)
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
        navigationItemVerticalArrangement = Arrangement.Center
    ) {
        AppNavHost(navController = navController)
    }
}

context(viewModel: SafetySheetViewModel)
@Composable
private fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = viewModel.startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.Sheet -> SafetySheetPage()
                    Destination.Preview -> PreviewPage()
                    Destination.Settings -> SettingsPage()
                    Destination.About -> AboutPage()
                }
            }
        }
    }
}