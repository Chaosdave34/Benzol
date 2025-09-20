package io.github.chaosdave34.benzol

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.chaosdave34.benzol.settings.Theme
import io.github.chaosdave34.benzol.ui.DisclaimerDialog
import io.github.chaosdave34.benzol.ui.Layout
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    val viewModel: SafetySheetViewModel = viewModel { SafetySheetViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val substances = remember { mutableStateListOf<Substance>() }
    val humanAndEnvironmentDanger = remember { mutableStateListOf<String>() }
    val rulesOfConduct = remember { mutableStateListOf<String>() }
    val inCaseOfDanger = remember { mutableStateListOf<String>() }
    val disposal = remember { mutableStateListOf<String>() }

    AppEnvironment {
        LocalAppLocale.provides(uiState.language.locale)

        LaunchedEffect(Unit) {
            viewModel.resetInput(
                substances = substances,
                humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                inCaseOfDanger = inCaseOfDanger,
                rulesOfConduct = rulesOfConduct,
                disposal = disposal
            )
            GHSPictogram.setBase64()
        }

        FileDialogs(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onImport = { data ->
                viewModel.importInput(data)

                substances.clear()
                humanAndEnvironmentDanger.clear()
                inCaseOfDanger.clear()
                rulesOfConduct.clear()
                disposal.clear()

                substances.addAll(data.substances)
                humanAndEnvironmentDanger.addAll(data.humanAndEnvironmentDanger)
                inCaseOfDanger.addAll(data.inCaseOfDanger)
                rulesOfConduct.addAll(data.rulesOfConduct)
                disposal.addAll(data.disposal)
            },
            onExport = {
                viewModel.exportInput(
                    substances = substances,
                    humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                    inCaseOfDanger = inCaseOfDanger,
                    rulesOfConduct = rulesOfConduct,
                    disposal = disposal
                )
            },
            settings = viewModel.settings,
            onCloseFileChooser = viewModel::closeFileChooser,
            onCloseFileSaver = viewModel::closeFileSaver,
            onClosePdfExport = viewModel::closePdfExport
        )

        val darkTheme = when (uiState.theme) {
            Theme.System -> isSystemInDarkTheme()
            Theme.Light -> false
            Theme.Dark -> true
        }

        MaterialExpressiveTheme(
            colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
        ) {
            DisclaimerDialog(
                visible = !uiState.disclaimerConfirmed,
                onConfirmation = viewModel::confirmDisclaimer
            )

            Layout(
                viewModel = viewModel,
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                substances = substances,
                humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                inCaseOfDanger = inCaseOfDanger,
                rulesOfConduct = rulesOfConduct,
                disposal = disposal
            )
        }
    }
}