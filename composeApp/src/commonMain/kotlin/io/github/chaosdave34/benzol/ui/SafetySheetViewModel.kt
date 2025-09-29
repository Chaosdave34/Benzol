package io.github.chaosdave34.benzol.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.chaosdave34.benzol.SupportedLanguage
import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.data.SafetySheetUiState
import io.github.chaosdave34.benzol.data.Substance
import io.github.chaosdave34.benzol.settings.Settings
import io.github.chaosdave34.benzol.settings.Theme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SafetySheetViewModel(
    val startDestination: Destination
) : ViewModel() {
    val settings = Settings()

    private val _uiState: MutableStateFlow<SafetySheetUiState> = MutableStateFlow(SafetySheetUiState(startDestination, settings))
    val uiState: StateFlow<SafetySheetUiState> = _uiState.asStateFlow()

    private val _inputState: MutableStateFlow<SafetySheetInputState> = MutableStateFlow(SafetySheetInputState())
    val inputState = _inputState.asStateFlow()

    fun setSelectedDestination(destination: Destination) {
        _uiState.update { it.copy(selectedDestination = destination) }
    }

    private fun setFileChooser(value: Boolean) {
        _uiState.update { it.copy(fileChooserVisible = value) }
    }

    fun openFileChooser() = setFileChooser(true)

    fun closeFileChooser() = setFileChooser(false)

    private fun setFileSaver(visible: Boolean) {
        _uiState.update { it.copy(fileSaverVisible = visible) }
    }

    fun openFileSaver() = setFileSaver(true)

    fun closeFileSaver() = setFileSaver(false)

    private fun setPdExport(visible: Boolean) {
        _uiState.update { it.copy(pdfExportVisible = visible) }
    }

    fun openPdfExport() = setPdExport(true)

    fun closePdfExport() = setPdExport(false)

    fun confirmDisclaimer() {
        _uiState.update { it.copy(disclaimerConfirmed = true) }
        settings.disclaimerConfirmed = true
    }

    fun setTheme(value: Theme) {
        _uiState.update { it.copy(theme = value) }
        settings.theme = value
    }

    fun setLanguage(language: SupportedLanguage) {
        _uiState.update { it.copy(language = language) }
        settings.language = language
    }

    fun setExportUrl(exportUrl: String) {
        _uiState.update { it.copy(exportUrl = exportUrl) }
        settings.exportUrl = exportUrl
    }

    fun setFilename(value: String) {
        _inputState.update { it.copy(filename = value) }
    }

    fun setDocumentTitle(value: String) {
        _inputState.update {
            it.copy(documentTitle = value)
        }
    }

    fun setOrganisation(value: String) {
        _inputState.update { it.copy(organisation = value) }
    }

    fun setCourse(value: String) {
        _inputState.update { it.copy(course = value) }
    }

    fun setName(value: String) {
        _inputState.update { it.copy(name = value) }
    }

    fun setPlace(value: String) {
        _inputState.update { it.copy(place = value) }
    }

    fun setAssistant(value: String) {
        _inputState.update { it.copy(assistant = value) }
    }

    fun setPreparation(value: String) {
        _inputState.update { it.copy(preparation = value) }
    }

    // Substances
    fun addSubstance(substance: Substance) {
        _inputState.update { it.copy(substances = it.substances + substance) }
    }

    fun removeSubstance(index: Int) {
        _inputState.update {
            it.copy(
                substances = it.substances.toMutableList().apply {
                    if (index in indices) removeAt(index)
                }
            )
        }
    }

    fun updateSubstance(index: Int, newSubstance: Substance) {
        _inputState.update { state ->
            state.copy(substances = state.substances.toMutableList().apply { set(index, newSubstance) })
        }
    }

    // Human and Environment Danger
    fun addHumanAndEnvironmentDanger(item: String) {
        _inputState.update { it.copy(humanAndEnvironmentDanger = it.humanAndEnvironmentDanger + item) }
    }

    fun removeHumanAndEnvironmentDanger(index: Int) {
        _inputState.update {
            it.copy(
                humanAndEnvironmentDanger = it.humanAndEnvironmentDanger.toMutableList().apply {
                    if (index in indices) removeAt(index)
                }
            )
        }
    }

    fun updateHumanAndEnvironmentDanger(index: Int, newValue: String) {
        _inputState.update { state ->
            state.copy(humanAndEnvironmentDanger = state.humanAndEnvironmentDanger.toMutableList().apply { set(index, newValue) })
        }
    }

    // In Case of Danger
    fun addInCaseOfDanger(item: String) {
        _inputState.update { it.copy(inCaseOfDanger = it.inCaseOfDanger + item) }
    }

    fun removeInCaseOfDanger(index: Int) {
        _inputState.update {
            it.copy(
                inCaseOfDanger = it.inCaseOfDanger.toMutableList().apply {
                    if (index in indices) removeAt(index)
                }
            )
        }
    }

    fun updateInCaseOfDanger(index: Int, newValue: String) {
        _inputState.update { state ->
            state.copy(inCaseOfDanger = state.inCaseOfDanger.toMutableList().apply { set(index, newValue) })
        }
    }

    // Rules of Conduct
    fun addRuleOfConduct(item: String) {
        _inputState.update { it.copy(rulesOfConduct = it.rulesOfConduct + item) }
    }

    fun removeRuleOfConduct(index: Int) {
        _inputState.update {
            it.copy(
                rulesOfConduct = it.rulesOfConduct.toMutableList().apply {
                    if (index in indices) removeAt(index)
                }
            )
        }
    }

    fun updateRuleOfConduct(index: Int, newValue: String) {
        _inputState.update { state ->
            state.copy(rulesOfConduct = state.rulesOfConduct.toMutableList().apply { set(index, newValue) })
        }
    }

    // Disposal
    fun addDisposal(item: String) {
        _inputState.update { it.copy(disposal = it.disposal + item) }
    }

    fun removeDisposal(index: Int) {
        _inputState.update {
            it.copy(
                disposal = it.disposal.toMutableList().apply {
                    if (index in indices) removeAt(index)
                }
            )
        }
    }

    fun updateDisposal(index: Int, newValue: String) {
        _inputState.update { state ->
            state.copy(disposal = state.disposal.toMutableList().apply { set(index, newValue) })
        }
    }

    fun resetInput() {
        viewModelScope.launch {
            _inputState.update { _ ->
                SafetySheetInputState.default()
            }
        }
    }

    fun importData(data: SafetySheetInputState) {
        _inputState.update {
            data
        }
    }
}