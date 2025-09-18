package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.chaosdave34.benzol.ui.Layout
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel

@Composable
fun App() {
    val viewModel: SafetySheetViewModel = viewModel { SafetySheetViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    AppEnvironment {
        LocalAppLocale.provides(uiState.language.locale)

        LaunchedEffect(Unit) {
            viewModel.setDefaultInputValues()
            GHSPictogram.setBase64()
        }

        FileDialogs(viewModel = viewModel)

        Layout(viewModel = viewModel)
    }
}