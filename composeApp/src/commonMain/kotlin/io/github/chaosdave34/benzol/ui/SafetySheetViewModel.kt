package io.github.chaosdave34.benzol.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.Settings
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.SupportedLanguage
import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.data.SafetySheetUiState
import io.github.chaosdave34.benzol.files.InputData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getStringArray

class SafetySheetViewModel(
    val settings: Settings,
    val resourceEnvironment: ResourceEnvironment
) : ViewModel() {
    private val _uiState: MutableStateFlow<SafetySheetUiState> = MutableStateFlow(SafetySheetUiState(settings))
    val uiState: StateFlow<SafetySheetUiState> = _uiState.asStateFlow()

    private val _snackBarHostState = MutableStateFlow(SnackbarHostState())
    val snackbarHostState = _snackBarHostState.asStateFlow()

    private val _inputState = MutableStateFlow(SafetySheetInputState())
    val inputState = _inputState.asStateFlow()

    private val _substances = MutableStateFlow(mutableStateListOf<Substance>())
    val substances = _substances.asStateFlow()

    private val _humanAndEnvironmentDanger = MutableStateFlow(mutableStateListOf<String>())
    val humanAndEnvironmentDanger = _humanAndEnvironmentDanger.asStateFlow()

    private val _rulesOfConduct = MutableStateFlow(mutableStateListOf<String>())
    val rulesOfConduct = _rulesOfConduct.asStateFlow()

    private val _inCaseOfDanger = MutableStateFlow(mutableStateListOf<String>())
    val inCaseOfDanger = _inCaseOfDanger.asStateFlow()

    private val _disposal = MutableStateFlow(mutableStateListOf<String>())
    val disposal = _disposal.asStateFlow()

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

    fun setDarkMode(value: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                darkMode = value
            )
        }
        settings.darkTheme = value
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

    private fun setSubstances(value: List<Substance>) {
        _substances.update { _ ->
            value.toMutableStateList()
        }
    }

    private fun setHumanAndEnvironmentDanger(value: List<String>) {
        _humanAndEnvironmentDanger.update { _ ->
            value.toMutableStateList()
        }
    }

    private fun setRulesOfConduct(value: List<String>) {
        _rulesOfConduct.update { _ ->
            value.toMutableStateList()
        }
    }

    private fun setInCaseOfDanger(value: List<String>) {
        _inCaseOfDanger.update { _ ->
            value.toMutableStateList()
        }
    }

    private fun setDisposal(value: List<String>) {
        _disposal.update { _ ->
            value.toMutableStateList()
        }
    }

    fun resetInput() {
        setFilename("")
        setDocumentTitle("")
        setOrganisation("")
        setCourse("")
        setName("")
        setPlace("")
        setAssistant("")
        setPreparation("")

        setSubstances(emptyList())
        setHumanAndEnvironmentDanger(emptyList())
        setRulesOfConduct(emptyList())
        setInCaseOfDanger(emptyList())
        setDisposal(emptyList())
    }

    suspend fun setDefaultInputValues() {
        setDocumentTitle(getString(resourceEnvironment, Res.string.document_title_default))
        setOrganisation(getString(resourceEnvironment, Res.string.organisation_default))
        setCourse(getString(resourceEnvironment, Res.string.course_default))
        setInCaseOfDanger(getStringArray(resourceEnvironment, Res.array.in_case_of_danger_defaults))
        setRulesOfConduct(getStringArray(resourceEnvironment, Res.array.rules_of_conduct_defaults))
    }

    fun importInput(data: InputData) {
        setFilename(data.filename)
        setDocumentTitle(data.documentTitle)
        setOrganisation(data.organisation)
        setCourse(data.course)
        setName(data.name)
        setPlace(data.place)
        setAssistant(data.assistant)
        setPreparation(data.preparation)

        setSubstances(data.substances)
        setHumanAndEnvironmentDanger(data.humanAndEnvironmentDanger)
        setRulesOfConduct(data.rulesOfConduct)
        setInCaseOfDanger(data.inCaseOfDanger)
        setDisposal(data.disposal)
    }

    fun exportInput() = InputData(
        filename = _inputState.value.filename,
        documentTitle = _inputState.value.documentTitle,
        organisation = _inputState.value.organisation,
        course = _inputState.value.course,
        name = _inputState.value.name,
        place = _inputState.value.place,
        assistant = _inputState.value.assistant,
        preparation = _inputState.value.preparation,
        substances = _substances.value,
        humanAndEnvironmentDanger = _humanAndEnvironmentDanger.value,
        rulesOfConduct = _rulesOfConduct.value,
        inCaseOfDanger = _inCaseOfDanger.value,
        disposal = _disposal.value
    )
}