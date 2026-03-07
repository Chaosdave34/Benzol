package io.github.chaosdave34.benzol

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.files.createHtml
import io.github.chaosdave34.benzol.files.export.FileUtils
import io.github.chaosdave34.benzol.files.export.FileUtils.encode
import io.github.chaosdave34.benzol.files.export.Savable
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.download
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.*

private val client = HttpClient()

private suspend fun saveFile(
    savable: Savable,
    filename: String,
    resourceEnvironment: ResourceEnvironment
) {
    val output = savable.encode()

    FileKit.download(
        bytes = output.toByteArray(),
        fileName = filename.ifEmpty { getString(resourceEnvironment, Res.string.unnamed_file) } + FileUtils.FILE_EXTENSION
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
                    savable = inputState,
                    filename = inputState.filename,
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
actual fun FloatingActionButtonMenuScope.SaveFileFabButton(
    inputState: SafetySheetInputState,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val resourceEnvironment = rememberResourceEnvironment()

    FloatingActionButtonMenuItem(
        onClick = {
            scope.launch {
                saveFile(
                    savable = inputState,
                    filename = inputState.filename,
                    resourceEnvironment = resourceEnvironment
                )
            }
            onClick()
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
    inputState: SafetySheetInputState,
    exportUrl: String
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
        Icon(vectorResource(Res.drawable.picture_as_pdf_filled), contentDescription = stringResource(Res.string.export_file))
    }
}

@OptIn(markerClass = [ExperimentalMaterial3ExpressiveApi::class])
@Composable
actual fun FloatingActionButtonMenuScope.ExportFileFabButton(
    inputState: SafetySheetInputState,
    exportUrl: String,
    onClick: () -> Unit
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
            onClick()
        },
        icon = { ExportFileIcon() },
        text = { Text(stringResource(Res.string.export_file)) }
    )
}