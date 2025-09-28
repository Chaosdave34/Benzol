package io.github.chaosdave34.benzol

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.chaosdave34.benzol.data.GHSPictogram
import io.github.chaosdave34.benzol.settings.Theme
import io.github.chaosdave34.benzol.ui.Destination
import io.github.chaosdave34.benzol.ui.DisclaimerDialog
import io.github.chaosdave34.benzol.ui.Layout
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    val startDestination = Destination.Sheet

    val viewModel: SafetySheetViewModel = viewModel { SafetySheetViewModel(startDestination) }
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    AppEnvironment {
        LocalAppLocale.provides(uiState.language.locale)

        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {

            context(viewModel) {
                LaunchedEffect(Unit) {
                    viewModel.resetInput()
                    GHSPictogram.setBase64()
                }

                FileDialogs()

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

                    Layout()
                }
            }
        }
    }
}