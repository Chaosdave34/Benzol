package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.chaosdave34.benzol.ui.Layout
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import org.jetbrains.compose.resources.rememberResourceEnvironment

@Composable
fun App() {
    val language = SupportedLanguage.fromLocale(getSettings().getStringOrNull("language")) ?: SupportedLanguage.GERMAN

    AppEnvironment {
        LocalAppLocale.provides(language.locale)

        val resourceEnvironment = rememberResourceEnvironment()
        val viewModel: SafetySheetViewModel = viewModel { SafetySheetViewModel(resourceEnvironment) }

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