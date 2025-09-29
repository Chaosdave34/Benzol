package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.files.CaBr2File
import io.github.chaosdave34.benzol.files.createHtml
import io.github.chaosdave34.benzol.settings.Settings
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.rememberResourceEnvironment
import org.jetbrains.compose.resources.stringResource

context(viewModel: SafetySheetViewModel)
@Composable
fun FileDialogs() {
    val scope = rememberCoroutineScope()
    val resourceEnvironment = rememberResourceEnvironment()
    val snackbarHostState = LocalSnackbarHostState.current

    val settings = viewModel.settings
    val uiState by viewModel.uiState.collectAsState()
    val inputData by viewModel.inputState.collectAsState()

    val unnamed = stringResource(Res.string.unnamed_file)
    val failedToLoadFile = stringResource(Res.string.failed_to_load_file)
    val pdfExportSuccess = stringResource(Res.string.pdf_export_success)
    val pdfExportFailed = stringResource(Res.string.pdf_export_failed)

    if (uiState.fileChooserVisible) {
        FileChooser(
            scope = scope,
            settings = settings,
            onSelect = { json, fileName ->
                if (json != null) {
                    val caBr2File = CaBr2File.fromJson(json)
                    if (caBr2File != null) {
                        viewModel.importCaBr2(fileName, caBr2File)
                        return@FileChooser
                    }
                }
                scope.launch {
                    snackbarHostState.showSnackbar(failedToLoadFile)
                }
            },
            onClose = viewModel::closeFileChooser
        )
    }

    if (uiState.fileSaverVisible) { // Only trim input, linebreaks should be saved
        FileSaver(
            scope = scope,
            settings = settings,
            fileName = if (inputData.filename.isEmpty()) "$unnamed.cb2" else "${inputData.filename}.cb2",
            output = {
                val header = CaBr2File.CaBr2Data.Header(
                    inputData.documentTitle.trim(),
                    inputData.organisation.trim(),
                    inputData.course.trim(),
                    inputData.name.trim(),
                    inputData.place.trim(),
                    inputData.assistant.trim(),
                    inputData.preparation.trim()
                )

                val content = CaBr2File.CaBr2Data(
                    header,
                    inputData.substances.map { CaBr2File.CaBr2Data.SubstanceData.export(it) },
                    inputData.humanAndEnvironmentDanger.map { it.trim() },
                    inputData.rulesOfConduct.map { it.trim() },
                    inputData.inCaseOfDanger.map { it.trim() },
                    inputData.disposal.map { it.trim() }
                )

                CaBr2File.toJson(content)
            },
            onClose = viewModel::closeFileSaver
        )
    }

    if (uiState.pdfExportVisible) {
        if (PlatformUtils.IS_BROWSER && settings.exportUrl.isEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.no_pdf_export_url_provided))
            }
            return
        }

        PdfExport(
            scope = scope,
            settings = settings,
            fileName = if (inputData.filename.isEmpty()) "$unnamed.pdf" else "${inputData.filename}.pdf",
            html = { createHtml(inputData, resourceEnvironment) },
            onClose = { success ->
                viewModel.closePdfExport()
                scope.launch {
                    snackbarHostState.showSnackbar(if (success) pdfExportSuccess else pdfExportFailed)
                }
            }
        )
    }
}

@Composable
expect fun FileChooser(
    scope: CoroutineScope,
    settings: Settings,
    onSelect: (String?, String) -> Unit,
    onClose: () -> Unit
)

@Composable
expect fun FileSaver(
    scope: CoroutineScope,
    settings: Settings,
    fileName: String,
    output: () -> String,
    onClose: () -> Unit,
)

@Composable
expect fun PdfExport(
    scope: CoroutineScope,
    settings: Settings,
    fileName: String,
    html: suspend () -> String,
    onClose: (Boolean) -> Unit
)