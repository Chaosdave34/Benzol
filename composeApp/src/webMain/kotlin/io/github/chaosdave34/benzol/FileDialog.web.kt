package io.github.chaosdave34.benzol

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.files.CaBr2File
import io.github.chaosdave34.benzol.files.createHtml
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.download
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.rememberResourceEnvironment
import org.jetbrains.compose.resources.stringResource

private val client = HttpClient()

private suspend fun saveFile(
    inputState: SafetySheetInputState,
    resourceEnvironment: ResourceEnvironment
) {
    val output = CaBr2File.exportInputState(inputState)

    FileKit.download(
        bytes = output.toByteArray(),
        fileName = inputState.filename.ifEmpty { getString(resourceEnvironment, Res.string.unnamed_file) } + ".cb2"
    )
}

@Composable
actual fun SaveFileIconButton(inputState: SafetySheetInputState) {
    val scope = rememberCoroutineScope()
    val resourceEnvironment = rememberResourceEnvironment()

    IconButton(
        onClick = {
            scope.launch {
                saveFile(
                    inputState = inputState,
                    resourceEnvironment = resourceEnvironment
                )
            }
        }
    ) {
        SaveIcon()
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun FloatingActionButtonMenuScope.SaveFileFabButton(inputState: SafetySheetInputState) {
    val scope = rememberCoroutineScope()
    val resourceEnvironment = rememberResourceEnvironment()

    FloatingActionButtonMenuItem(
        onClick = {
            scope.launch {
                saveFile(
                    inputState = inputState,
                    resourceEnvironment = resourceEnvironment
                )
            }
        },
        icon = { SaveIcon() },
        text = { Text(stringResource(Res.string.save_file)) }
    )
}

private suspend fun exportPdf(
    exportUrl: String,
    inputState: SafetySheetInputState,
    resourceEnvironment: ResourceEnvironment,
    snackbarHostState: SnackbarHostState
) {
    if (exportUrl.isEmpty()) {
        snackbarHostState.showSnackbar(
            message = getString(resourceEnvironment, Res.string.no_pdf_export_url_provided),
            withDismissAction = true,
            duration = SnackbarDuration.Indefinite
        )
        return
    }

    val response = try {
        client.post(exportUrl) {
            setBody(createHtml(inputState, resourceEnvironment))
        }
    } catch (_: Throwable) {
        snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.pdf_export_failed))
        return
    }

    if (response.status == HttpStatusCode.OK) {
        FileKit.download(
            bytes = response.bodyAsBytes(),
            fileName = inputState.filename.ifEmpty { getString(resourceEnvironment, Res.string.unnamed_file) } + ".pdf"
        )

        snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.pdf_export_success))

    } else {
        snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.pdf_export_failed))
    }
}

@Composable
actual fun ExportFileIconButton(
    exportUrl: String,
    inputState: SafetySheetInputState
) {
    val scope = rememberCoroutineScope()
    val resourceEnvironment = rememberResourceEnvironment()
    val snackbarHostState = LocalSnackbarHostState.current

    IconButton(
        onClick = {
            scope.launch {
                exportPdf(
                    exportUrl = exportUrl,
                    inputState = inputState,
                    resourceEnvironment = resourceEnvironment,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    ) {
        Icon(Icons.Filled.PictureAsPdf, contentDescription = stringResource(Res.string.export_file))
    }
}

@OptIn(markerClass = [ExperimentalMaterial3ExpressiveApi::class])
@Composable
actual fun FloatingActionButtonMenuScope.ExportFileFabButton(
    inputState: SafetySheetInputState,
    exportUrl: String
) {
    val scope = rememberCoroutineScope()
    val resourceEnvironment = rememberResourceEnvironment()
    val snackbarHostState = LocalSnackbarHostState.current

    FloatingActionButtonMenuItem(
        onClick = {
            scope.launch {
                exportPdf(
                    exportUrl = exportUrl,
                    inputState = inputState,
                    resourceEnvironment = resourceEnvironment,
                    snackbarHostState = snackbarHostState
                )
            }
        },
        icon = { ExportFileIcon() },
        text = { Text(stringResource(Res.string.export_file)) }
    )
}