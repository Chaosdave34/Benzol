package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.data.SafetySheetUiState
import io.github.chaosdave34.benzol.ui.about.AboutPage
import io.github.chaosdave34.benzol.ui.preview.PreviewPage
import io.github.chaosdave34.benzol.ui.safetysheet.SafetySheetPage
import io.github.chaosdave34.benzol.ui.settings.SettingsPage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Layout(
    viewModel: SafetySheetViewModel,
    uiState: SafetySheetUiState,
    snackbarHostState: SnackbarHostState,
    substances: SnapshotStateList<Substance>,
    humanAndEnvironmentDanger: SnapshotStateList<String>,
    inCaseOfDanger: SnapshotStateList<String>,
    rulesOfConduct: SnapshotStateList<String>,
    disposal: SnapshotStateList<String>
) {
    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val navController = rememberNavController()
    val startDestination = Destination.Sheet
    var selectedDestination by rememberSaveable { mutableStateOf(startDestination) }

    var fabOrToolbarVisible by rememberSaveable { mutableStateOf(true) }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        fabOrToolbarVisible = destination.route in listOf(Destination.Sheet, Destination.Preview).map { it.route }
    }

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
                hostState = snackbarHostState,
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
                    if (!windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                        FloatingActionButtonMenu(
                            visible = fabOrToolbarVisible,
                            onResetInput = {
                                scope.launch {
                                    viewModel.resetInput(
                                        substances = substances,
                                        humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                                        inCaseOfDanger = inCaseOfDanger,
                                        rulesOfConduct = rulesOfConduct,
                                        disposal = disposal
                                    )
                                }
                            },
                            onChooseFile = viewModel::openFileChooser,
                            onSaveFile = viewModel::openFileSaver,
                            onExportPdf = viewModel::openPdfExport
                        )
                    }
                }
            ) {
                Box(Modifier.fillMaxSize()) {
                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination,
                        viewModel = viewModel,
                        uiState = uiState,
                        substances = substances,
                        humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                        rulesOfConduct = rulesOfConduct,
                        inCaseOfDanger = inCaseOfDanger,
                        disposal = disposal,
                        snackbarHostState = snackbarHostState,
                    )

                    if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                        Toolbar(
                            visible = fabOrToolbarVisible,
                            onResetInput = {
                                scope.launch {
                                    viewModel.resetInput(
                                        substances = substances,
                                        humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                                        inCaseOfDanger = inCaseOfDanger,
                                        rulesOfConduct = rulesOfConduct,
                                        disposal = disposal
                                    )
                                }
                            },
                            onChooseFile = viewModel::openFileChooser,
                            onSaveFile = viewModel::openFileSaver,
                            onExportPdf = viewModel::openPdfExport
                        )
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
    viewModel: SafetySheetViewModel,
    uiState: SafetySheetUiState,
    substances: SnapshotStateList<Substance>,
    humanAndEnvironmentDanger: SnapshotStateList<String>,
    rulesOfConduct: SnapshotStateList<String>,
    inCaseOfDanger: SnapshotStateList<String>,
    disposal: SnapshotStateList<String>,
    snackbarHostState: SnackbarHostState,
) {
    val inputState by viewModel.inputState.collectAsState()

    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.Sheet -> SafetySheetPage(
                        snackbarHostState = snackbarHostState,
                        inputState = inputState,
                        substances = substances,
                        humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                        rulesOfConduct = rulesOfConduct,
                        inCaseOfDanger = inCaseOfDanger,
                        disposal = disposal,
                        onFilenameChange = viewModel::setFilename,
                        onDocumentTitleChange = viewModel::setDocumentTitle,
                        onOrganisationChange = viewModel::setOrganisation,
                        onCourseChange = viewModel::setCourse,
                        onNameChange = viewModel::setName,
                        onPlaceChange = viewModel::setPlace,
                        onAssistantChange = viewModel::setAssistant,
                        onPreparationChange = viewModel::setPreparation,
                    )

                    Destination.Preview -> PreviewPage(
                        inputState = inputState,
                        substances = substances,
                        humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                        rulesOfConduct = rulesOfConduct,
                        inCaseOfDanger = inCaseOfDanger,
                        disposal = disposal
                    )

                    Destination.Settings -> SettingsPage(
                        uiState = uiState,
                        onLanguageChange = viewModel::setLanguage,
                        onThemeChange = viewModel::setTheme,
                        onExportUrlChange = viewModel::setExportUrl
                    )

                    Destination.About -> AboutPage()
                }
            }
        }
    }
}