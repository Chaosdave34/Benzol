package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.chaosdave34.benzol.ui.Layout
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import org.jetbrains.compose.resources.rememberResourceEnvironment

@Composable
fun App() {
    val settings = Settings()

    AppEnvironment {
        LocalAppLocale.provides(settings.language.locale)

        val resourceEnvironment = rememberResourceEnvironment()
        val viewModel: SafetySheetViewModel = viewModel {
            SafetySheetViewModel(
                settings = settings,
                resourceEnvironment = resourceEnvironment
            )
        }

        LaunchedEffect(Unit) {
            viewModel.setDefaultInputValues()
            GHSPictogram.setBase64()
        }

        FileDialogs(
            viewModel = viewModel,
            import = viewModel::importInput,
            export = viewModel::exportInput
        )

        Layout(viewModel = viewModel)
    }
}