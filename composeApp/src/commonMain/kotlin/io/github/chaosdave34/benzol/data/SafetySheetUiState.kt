package io.github.chaosdave34.benzol.data

import io.github.chaosdave34.benzol.SupportedLanguage

data class SafetySheetUiState(
    val fileChooserVisible: Boolean = false,
    val fileSaverVisible: Boolean = false,
    val pdfExportVisible: Boolean = false,
    val settingsVisible: Boolean = false,
    val disclaimerConfirmed: Boolean = false,
    val darkMode: Boolean,
    val language: SupportedLanguage,
)