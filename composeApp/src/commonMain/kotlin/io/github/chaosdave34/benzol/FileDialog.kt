package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.pdf_export_success
import benzol.composeapp.generated.resources.unnamed_file
import io.github.chaosdave34.benzol.files.CaBr2File
import io.github.chaosdave34.benzol.files.HtmlFile
import io.github.chaosdave34.benzol.files.InputData
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun FileDialogs(
    viewModel: SafetySheetViewModel,
    import: (InputData) -> Unit,
    export: () -> InputData,
) {
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState by viewModel.snackbarHostState.collectAsState()

    val unnamed = stringResource(Res.string.unnamed_file)

    if (uiState.fileChooserVisible) {
        FileChooser(
            coroutineScope = scope,
            result = { json, fileName ->
                if (json != null) {
                    val caBr2File = CaBr2File.fromJson(json)
                    if (caBr2File != null) {
                        val header = caBr2File.header

                        val inputDate = InputData(
                            fileName = fileName.replace("\\.[^.]*$".toRegex(), ""),
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

                        import(inputDate)
                    }
                }
            },
            onClose = viewModel::closeFileChooser
        )
    }

    if (uiState.fileSaverVisible) { // Only trim input, linebreaks should be saved
        FileSaver(
            coroutineScope = scope,
            output = {
                val inputData = export()

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

                val fileName = if (inputData.fileName.isEmpty()) "$unnamed.cb2" else "${inputData.fileName}.cb2"

                Pair(CaBr2File.toJson(content), fileName)
            },
            onClose = viewModel::closeFileSaver
        )
    }

    if (uiState.pdfExportVisible) { // Trim input and remove linebreaks
        PdfExport(
            coroutineScope = scope,
            output = {
                val inputData = export()

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
                    inputData.disposal.map { it.trim(); it.replace("\n", "") }
                )

                val fileName = if (inputData.fileName.isEmpty()) "$unnamed.pdf" else "${inputData.fileName}.pdf"

                Pair(htmlFile, fileName)
            },
            onClose = {
                viewModel.closePdfExport()
                scope.launch {
                    snackbarHostState.showSnackbar(getString(Res.string.pdf_export_success))
                }
            }
        )
    }
}


@Composable
expect fun FileChooser(
    coroutineScope: CoroutineScope,
    result: (String?, String) -> Unit,
    onClose: () -> Unit
)

@Composable
expect fun FileSaver(
    coroutineScope: CoroutineScope,
    output: () -> Pair<String, String>,
    onClose: () -> Unit,
)

@Composable
expect fun PdfExport(
    coroutineScope: CoroutineScope,
    output: () -> Pair<HtmlFile, String>,
    onClose: () -> Unit
)