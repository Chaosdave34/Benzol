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
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
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
    resourceEnvironment: ResourceEnvironment,
    snackbarHostState: SnackbarHostState
) {
    val output = savable.encode()

    FileKit.download(
        bytes = output.toByteArray(),
        fileName = filename.ifEmpty { getString(resourceEnvironment, Res.string.unnamed_file) } + ".${FileUtils.FILE_EXTENSION}"
    )

    snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.file_saved_success))
}

@Composable
actual fun SaveFileIconButton(inputState: SafetySheetInputState) {
    val scope = rememberCoroutineScope()
    val resourceEnvironment = rememberResourceEnvironment()
    val snackbarHostState = LocalSnackbarHostState.current

    IconButton(
        onClick = {
            scope.launch {
                saveFile(
                    savable = inputState,
                    filename = inputState.filename,
                    resourceEnvironment = resourceEnvironment,
                    snackbarHostState = snackbarHostState
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
    val snackbarHostState = LocalSnackbarHostState.current

    FloatingActionButtonMenuItem(
        onClick = {
            scope.launch {
                saveFile(
                    savable = inputState,
                    filename = inputState.filename,
                    resourceEnvironment = resourceEnvironment,
                    snackbarHostState = snackbarHostState
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
    snackbarHostState: SnackbarHostState,
    onLoadingChange: (Boolean) -> Unit
) {
    if (exportUrl.isEmpty()) {
        snackbarHostState.showSnackbar(
            message = getString(resourceEnvironment, Res.string.no_pdf_export_url_provided),
            withDismissAction = true,
            duration = SnackbarDuration.Indefinite
        )
        return
    }
    onLoadingChange(true)

    val response = try {
        client.post(exportUrl) {
            setBody(createHtml(inputState, resourceEnvironment))
        }
    } catch (_: Throwable) {
        onLoadingChange(false)
        snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.pdf_export_failed))
        return
    }

    if (response.status == HttpStatusCode.OK) {
        FileKit.download(
            bytes = response.bodyAsBytes(),
            fileName = inputState.filename.ifEmpty { getString(resourceEnvironment, Res.string.unnamed_file) } + ".pdf"
        )

        onLoadingChange(false)
        snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.pdf_export_success))
    } else {
        onLoadingChange(false)
        snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.pdf_export_failed))
    }
}

context(viewModel: SafetySheetViewModel)
@Composable
actual fun ExportFileIconButton() {
    val inputState by viewModel.inputState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val resourceEnvironment = rememberResourceEnvironment()
    val snackbarHostState = LocalSnackbarHostState.current

    IconButton(
        onClick = {
            scope.launch {
                exportPdf(
                    exportUrl = uiState.exportUrl,
                    inputState = inputState,
                    resourceEnvironment = resourceEnvironment,
                    snackbarHostState = snackbarHostState,
                    onLoadingChange = viewModel::setLoading
                )
            }
        }
    ) {
        Icon(vectorResource(Res.drawable.picture_as_pdf_filled), contentDescription = stringResource(Res.string.export_file))
    }
}

context(viewModel: SafetySheetViewModel)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
actual fun FloatingActionButtonMenuScope.ExportFileFabButton(
    onClick: () -> Unit,
) {
    val inputState by viewModel.inputState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val resourceEnvironment = rememberResourceEnvironment()
    val snackbarHostState = LocalSnackbarHostState.current

    FloatingActionButtonMenuItem(
        onClick = {
            scope.launch {
                exportPdf(
                    exportUrl = uiState.exportUrl,
                    inputState = inputState,
                    resourceEnvironment = resourceEnvironment,
                    snackbarHostState = snackbarHostState,
                    onLoadingChange = viewModel::setLoading
                )
            }
            onClick()
        },
        icon = { ExportFileIcon() },
        text = { Text(stringResource(Res.string.export_file)) }
    )
}