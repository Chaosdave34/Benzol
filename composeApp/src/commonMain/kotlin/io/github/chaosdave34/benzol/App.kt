package io.github.chaosdave34.benzol

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.chaosdave34.benzol.ui.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val viewModel: SafetySheetViewModel = viewModel { SafetySheetViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    AppEnvironment {
        LocalAppLocale.provides(uiState.language.locale)

        LaunchedEffect(Unit) {
            GHSPictogram.setBase64()
            viewModel.setDefaultInputValues()
        }

        FileDialogs(
            coroutineScope = coroutineScope,
            viewModel = viewModel,
            import = viewModel::importInput,
            export = viewModel::exportInput
        )

        MaterialTheme(
            colorScheme = if (uiState.darkMode) darkColorScheme() else lightColorScheme()
        ) {
            Settings(
                viewModel = viewModel
            )
            Disclaimer()

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = viewModel.snackbarHostState.value,
                        modifier = Modifier
                            .padding(bottom = 60.dp)
                            .fillMaxWidth(0.7f),
                    )
                }
            ) {
                Row(
                    Modifier.pointerInput(Unit) {
                        detectTapGestures {
                            focusManager.clearFocus()
                        }
                    }
                ) {
                    Sidebar(
                        viewModel = viewModel,
                        resetInput = {
                            viewModel.resetInput()
                            coroutineScope.launch {
                                viewModel.setDefaultInputValues()
                            }
                        }
                    )

                    Content(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}