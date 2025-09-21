package io.github.chaosdave34.benzol

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.SafetySheetData
import io.github.chaosdave34.benzol.data.SafetySheetUiState
import io.github.chaosdave34.benzol.files.CaBr2File
import io.github.chaosdave34.benzol.files.HtmlFile
import io.github.chaosdave34.benzol.settings.Settings
import io.ktor.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.rememberResourceEnvironment
import org.jetbrains.compose.resources.stringResource

@Composable
fun FileDialogs(
    uiState: SafetySheetUiState,
    settings: Settings,
    snackbarHostState: SnackbarHostState,
    onImport: (SafetySheetData) -> Unit,
    onExport: () -> SafetySheetData,
    onCloseFileChooser: () -> Unit,
    onCloseFileSaver: () -> Unit,
    onClosePdfExport: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val resourceEnvironment = rememberResourceEnvironment()

    val unnamed = stringResource(Res.string.unnamed_file)
    val failedToLoadFile = stringResource(Res.string.failed_to_load_file)
    val pdfExportSuccess = stringResource(Res.string.pdf_export_success)
    val pdfExportFailed = stringResource(Res.string.pdf_export_failed)

    if (uiState.fileChooserVisible) {
        FileChooser(
            coroutineScope = scope,
            settings = settings,
            result = { json, fileName ->
                if (json != null) {
                    val caBr2File = CaBr2File.fromJson(json)
                    if (caBr2File != null) {
                        val header = caBr2File.header

                        val inputDate = SafetySheetData(
                            filename = fileName.replace("\\.[^.]*$".toRegex(), ""),
                            documentTitle = header.documentTitle,
                            organisation = header.organisation,
                            course = header.labCourse,
                            name = header.name,
                            place = header.place,
                            assistant = header.assistant,
                            preparation = header.preparation,
                            substances = caBr2File.substanceData.map { it.import() },
                            humanAndEnvironmentDanger = caBr2File.humanAndEnvironmentDanger,
                            rulesOfConduct = caBr2File.rulesOfConduct,
                            inCaseOfDanger = caBr2File.inCaseOfDanger,
                            disposal = caBr2File.disposal
                        )

                        onImport(inputDate)
                        return@FileChooser
                    }
                }
                scope.launch {
                    snackbarHostState.showSnackbar(failedToLoadFile)
                }
            },
            onClose = onCloseFileChooser
        )
    }

    if (uiState.fileSaverVisible) { // Only trim input, linebreaks should be saved
        FileSaver(
            coroutineScope = scope,
            settings = settings,
            output = {
                val inputData = onExport()
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

                val fileName = if (inputData.filename.isEmpty()) "$unnamed.cb2" else "${inputData.filename}.cb2"

                Pair(CaBr2File.toJson(content), fileName)
            },
            onClose = onCloseFileSaver
        )
    }

    if (uiState.pdfExportVisible) { // Trim input and remove linebreaks
        if (PlatformUtils.IS_BROWSER && settings.exportUrl.isEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(getString(resourceEnvironment, Res.string.no_pdf_export_url_provided))
            }
            return
        }

        PdfExport(
            coroutineScope = scope,
            settings = settings,
            output = {
                val inputData = onExport()

                val htmlFile = HtmlFile(
                    inputData.documentTitle.trim(),
                    inputData.organisation.trim(),
                    inputData.course.trim(),
                    inputData.name.trim(),
                    inputData.place.trim(),
                    inputData.assistant.trim(),
                    inputData.preparation.trim(),
                    inputData.substances,
                    inputData.humanAndEnvironmentDanger.map { it.trim(); it.replace("\n", "") },
                    inputData.rulesOfConduct.map { it.trim(); it.replace("\n", "") },
                    inputData.inCaseOfDanger.map { it.trim(); it.replace("\n", "") },
                    inputData.disposal.map { it.trim(); it.replace("\n", "") },
                    resourceEnvironment
                )

                val fileName = if (inputData.filename.isEmpty()) "$unnamed.pdf" else "${inputData.filename}.pdf"

                Pair(htmlFile, fileName)
            },
            onClose = { success ->
                onClosePdfExport()
                scope.launch {
                    snackbarHostState.showSnackbar(if (success) pdfExportSuccess else pdfExportFailed)
                }
            }
        )
    }
}


@Composable
expect fun FileChooser(
    coroutineScope: CoroutineScope,
    settings: Settings,
    result: (String?, String) -> Unit,
    onClose: () -> Unit
)

@Composable
expect fun FileSaver(
    coroutineScope: CoroutineScope,
    settings: Settings,
    output: () -> Pair<String, String>,
    onClose: () -> Unit,
)

@Composable
expect fun PdfExport(
    coroutineScope: CoroutineScope,
    settings: Settings,
    output: () -> Pair<HtmlFile, String>,
    onClose: (Boolean) -> Unit
)