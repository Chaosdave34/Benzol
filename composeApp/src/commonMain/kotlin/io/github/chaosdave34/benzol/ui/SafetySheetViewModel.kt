package io.github.chaosdave34.benzol.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.SupportedLanguage
import io.github.chaosdave34.benzol.data.SafetySheetData
import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.data.SafetySheetUiState
import io.github.chaosdave34.benzol.settings.Settings
import io.github.chaosdave34.benzol.settings.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getStringArray

class SafetySheetViewModel : ViewModel() {
    val settings = Settings()

    private val _uiState: MutableStateFlow<SafetySheetUiState> = MutableStateFlow(SafetySheetUiState(settings))
    val uiState: StateFlow<SafetySheetUiState> = _uiState.asStateFlow()

    private val _inputState = MutableStateFlow(SafetySheetInputState())
    val inputState = _inputState.asStateFlow()

    private fun setFileChooser(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                fileChooserVisible = value
            )
        }
    }

    fun openFileChooser() = setFileChooser(true)

    fun closeFileChooser() = setFileChooser(false)

    private fun setFileSaver(visible: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                fileSaverVisible = visible
            )
        }
    }

    fun openFileSaver() = setFileSaver(true)

    fun closeFileSaver() = setFileSaver(false)

    private fun setPdExport(visible: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                pdfExportVisible = visible
            )
        }
    }

    fun openPdfExport() = setPdExport(true)

    fun closePdfExport() = setPdExport(false)

    fun confirmDisclaimer() {
        _uiState.update { currentState ->
            currentState.copy(
                disclaimerConfirmed = true
            )
        }
        settings.disclaimerConfirmed = true
    }

    fun setTheme(value: Theme) {
        _uiState.update { currentState ->
            currentState.copy(
                theme = value
            )
        }
        settings.theme = value
    }

    fun setLanguage(language: SupportedLanguage) {
        _uiState.update { currentState ->
            currentState.copy(
                language = language
            )
        }
        settings.language = language
    }

    fun setExportUrl(exportUrl: String) {
        _uiState.update { currentState ->
            currentState.copy(
                exportUrl = exportUrl
            )
        }
        settings.exportUrl = exportUrl
    }

    fun setFilename(value: String) {
        _inputState.update { currentState ->
            currentState.copy(
                filename = value
            )
        }
    }

    fun setDocumentTitle(value: String) {
        _inputState.update { currentState ->
            currentState.copy(
                documentTitle = value
            )
        }
    }

    fun setOrganisation(value: String) {
        _inputState.update { currentState ->
            currentState.copy(
                organisation = value
            )
        }
    }

    fun setCourse(value: String) {
        _inputState.update { currentState ->
            currentState.copy(
                course = value
            )
        }
    }

    fun setName(value: String) {
        _inputState.update { currentState ->
            currentState.copy(
                name = value
            )
        }
    }

    fun setPlace(value: String) {
        _inputState.update { currentState ->
            currentState.copy(
                place = value
            )
        }
    }

    fun setAssistant(value: String) {
        _inputState.update { currentState ->
            currentState.copy(
                assistant = value
            )
        }
    }

    fun setPreparation(value: String) {
        _inputState.update { currentState ->
            currentState.copy(
                preparation = value
            )
        }
    }

    suspend fun resetInput(
        substances: SnapshotStateList<Substance>,
        humanAndEnvironmentDanger: SnapshotStateList<String>,
        inCaseOfDanger: SnapshotStateList<String>,
        rulesOfConduct: SnapshotStateList<String>,
        disposal: SnapshotStateList<String>
    ) {
        setFilename("")
        setDocumentTitle(getString(Res.string.document_title_default))
        setOrganisation(getString(Res.string.organisation_default))
        setCourse(getString(Res.string.course_default))
        setName("")
        setPlace("")
        setAssistant("")
        setPreparation("")

        substances.clear()
        humanAndEnvironmentDanger.clear()

        inCaseOfDanger.clear()
        inCaseOfDanger.addAll(getStringArray(Res.array.in_case_of_danger_defaults))

        rulesOfConduct.clear()
        rulesOfConduct.addAll(getStringArray(Res.array.rules_of_conduct_defaults))

        disposal.clear()
    }

    fun importInput(data: SafetySheetData) {
        _inputState.update { currentState ->
            currentState.copy(
                filename = data.filename,
                documentTitle = data.documentTitle,
                course = data.course,
                name = data.name,
                place = data.place,
                assistant = data.assistant,
                preparation = data.preparation
            )
        }
    }

    fun exportInput(
        substances: SnapshotStateList<Substance>,
        humanAndEnvironmentDanger: SnapshotStateList<String>,
        inCaseOfDanger: SnapshotStateList<String>,
        rulesOfConduct: SnapshotStateList<String>,
        disposal: SnapshotStateList<String>
    ) = SafetySheetData(
        filename = _inputState.value.filename,
        documentTitle = _inputState.value.documentTitle,
        organisation = _inputState.value.organisation,
        course = _inputState.value.course,
        name = _inputState.value.name,
        place = _inputState.value.place,
        assistant = _inputState.value.assistant,
        preparation = _inputState.value.preparation,
        substances = substances,
        humanAndEnvironmentDanger = humanAndEnvironmentDanger,
        rulesOfConduct = rulesOfConduct,
        inCaseOfDanger = inCaseOfDanger,
        disposal = disposal
    )
}