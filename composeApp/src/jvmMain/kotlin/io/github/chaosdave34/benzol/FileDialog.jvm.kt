package io.github.chaosdave34.benzol

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.files.createHtml
import io.github.chaosdave34.benzol.files.export.FileUtils
import io.github.chaosdave34.benzol.files.export.FileUtils.encode
import io.github.chaosdave34.benzol.files.export.Savable
import io.github.chaosdave34.benzol.files.htmlToPdf
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import io.github.vinceglb.filekit.dialogs.compose.SaverResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.write
import io.github.vinceglb.filekit.writeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.rememberResourceEnvironment
import org.jetbrains.compose.resources.stringResource

@Composable
private fun rememberFileSaver(
    savable: Savable
): SaverResultLauncher {
    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    return rememberFileSaverLauncher { file ->
        if (file != null) {
            val output = savable.encode()
            scope.launch(context = Dispatchers.IO) {
                file.writeString(output)
                snackbarHostState.showSnackbar(getString(Res.string.file_saved_success))
            }
        }
    }
}

@Composable
actual fun SaveFileIconButton(
    inputState: SafetySheetInputState
) {
    val launcher = rememberFileSaver(inputState)
    val unnamed = stringResource(Res.string.unnamed_file)

    IconButton(
        onClick = {
            launcher.launch(
                suggestedName = inputState.filename.ifEmpty { unnamed },
                extension = FileUtils.FILE_EXTENSION
            )
        },
    ) {
        SaveIcon()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun FloatingActionButtonMenuScope.SaveFileFabButton(
    inputState: SafetySheetInputState,
    onClick: () -> Unit
) {
    val launcher = rememberFileSaver(inputState)
    val unnamed = stringResource(Res.string.unnamed_file)

    FloatingActionButtonMenuItem(
        onClick = {
            launcher.launch(
                suggestedName = inputState.filename.ifEmpty { unnamed },
                extension = FileUtils.FILE_EXTENSION
            )
            onClick()
        },
        icon = { SaveIcon() },
        text = { Text(stringResource(Res.string.save_file)) }
    )
}

context(viewModel: SafetySheetViewModel)
@Composable
private fun rememberPdfExporter(): SaverResultLauncher {
    val inputState by viewModel.inputState.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val resourceEnvironment = rememberResourceEnvironment()

    return rememberFileSaverLauncher { file ->
        if (file != null) {
            viewModel.setLoading(true)
            scope.launch(context = Dispatchers.IO) {
                val html = createHtml(inputState, resourceEnvironment)
                file.write(htmlToPdf(html))

                viewModel.setLoading(false)
                snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.pdf_export_success))
            }
        }
    }
}

context(viewModel: SafetySheetViewModel)
@Composable
actual fun ExportFileIconButton() {
    val inputState by viewModel.inputState.collectAsState()
    val launcher = rememberPdfExporter()
    val unnamed = stringResource(Res.string.unnamed_file)

    IconButton(
        onClick = {
            launcher.launch(
                suggestedName = inputState.filename.ifEmpty { unnamed },
                extension = "pdf"
            )
        }
    ) {
        ExportFileIcon()
    }
}

context(viewModel: SafetySheetViewModel)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun FloatingActionButtonMenuScope.ExportFileFabButton(
    onClick: () -> Unit
) {
    val inputState by viewModel.inputState.collectAsState()
    val launcher = rememberPdfExporter()
    val unnamed = stringResource(Res.string.unnamed_file)

    FloatingActionButtonMenuItem(
        onClick = {
            launcher.launch(
                suggestedName = inputState.filename.ifEmpty { unnamed },
                extension = "pdf"
            )
            onClick()
        },
        icon = { ExportFileIcon() },
        text = { Text(stringResource(Res.string.export_file)) }
    )
}