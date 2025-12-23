package io.github.chaosdave34.benzol

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenuScope
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.files.CaBr2File
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import io.github.vinceglb.filekit.dialogs.compose.PickerResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.rememberResourceEnvironment
import org.jetbrains.compose.resources.stringResource

@Composable
fun SaveIcon() {
    Icon(Icons.Filled.Save, contentDescription = stringResource(Res.string.save_file))
}

@Composable
fun ExportFileIcon() {
    Icon(Icons.Filled.PictureAsPdf, contentDescription = stringResource(Res.string.export_file))
}

@Composable
expect fun SaveFileIconButton(
    inputState: SafetySheetInputState
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
expect fun FloatingActionButtonMenuScope.SaveFileFabButton(
    inputState: SafetySheetInputState,
    onClick: () -> Unit
)

@Composable
expect fun ExportFileIconButton(
    inputState: SafetySheetInputState,
    exportUrl: String
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
expect fun FloatingActionButtonMenuScope.ExportFileFabButton(
    inputState: SafetySheetInputState,
    exportUrl: String,
    onClick: () -> Unit
)

context(viewModel: SafetySheetViewModel)
@Composable
fun rememberFilePicker(): PickerResultLauncher {
    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val resourceEnvironment = rememberResourceEnvironment()

    return rememberFilePickerLauncher(
        title = stringResource(Res.string.open_file)
    ) { file ->
        scope.launch {
            if (file != null) {
                val caBr2File = CaBr2File.fromJson(file.readString())
                if (caBr2File != null) {
                    viewModel.importCaBr2(file.nameWithoutExtension, caBr2File)
                } else {
                    snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.failed_to_load_file))
                }
            }
        }
    }
}