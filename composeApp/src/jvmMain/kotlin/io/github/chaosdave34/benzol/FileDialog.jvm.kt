package io.github.chaosdave34.benzol

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.files.CaBr2File
import io.github.chaosdave34.benzol.files.createHtml
import io.github.chaosdave34.benzol.files.htmlToPdf
import io.github.vinceglb.filekit.dialogs.compose.SaverResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.write
import io.github.vinceglb.filekit.writeString
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.rememberResourceEnvironment
import org.jetbrains.compose.resources.stringResource

@Composable
private fun rememberFileSaver(
    inputState: SafetySheetInputState
): SaverResultLauncher {
    val scope = rememberCoroutineScope()

    return rememberFileSaverLauncher { file ->
        if (file != null) {
            val output = CaBr2File.exportInputState(inputState)

            scope.launch { file.writeString(output) }
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
                extension = "cb2"
            )
        },
    ) {
        SaveIcon()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun FloatingActionButtonMenuScope.SaveFileFabButton(inputState: SafetySheetInputState) {
    val launcher = rememberFileSaver(inputState)
    val unnamed = stringResource(Res.string.unnamed_file)

    FloatingActionButtonMenuItem(
        onClick = {
            launcher.launch(
                suggestedName = inputState.filename.ifEmpty { unnamed },
                extension = "cb2"
            )
        },
        icon = { SaveIcon() },
        text = { Text(stringResource(Res.string.save_file)) }
    )
}

@Composable
private fun rememberPdfExporter(
    inputState: SafetySheetInputState
): SaverResultLauncher {
    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val resourceEnvironment = rememberResourceEnvironment()

    return rememberFileSaverLauncher { file ->
        scope.launch {
            if (file != null) {
                val html = createHtml(inputState, resourceEnvironment)
                file.write(htmlToPdf(html))

                snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.pdf_export_success))
            }
        }
    }
}

@Composable
actual fun ExportFileIconButton(
    exportUrl: String,
    inputState: SafetySheetInputState
) {
    val launcher = rememberPdfExporter(inputState)
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun FloatingActionButtonMenuScope.ExportFileFabButton(
    inputState: SafetySheetInputState,
    exportUrl: String
) {
    val launcher = rememberPdfExporter(inputState)
    val unnamed = stringResource(Res.string.unnamed_file)

    FloatingActionButtonMenuItem(
        onClick = {
            launcher.launch(
                suggestedName = inputState.filename.ifEmpty { unnamed },
                extension = "pdf"
            )
        },
        icon = { ExportFileIcon() },
        text = { Text(stringResource(Res.string.export_file)) }
    )
}
