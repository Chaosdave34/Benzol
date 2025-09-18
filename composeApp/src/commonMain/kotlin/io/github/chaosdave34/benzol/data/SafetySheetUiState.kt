package io.github.chaosdave34.benzol.data

import io.github.chaosdave34.benzol.Settings
import io.github.chaosdave34.benzol.SupportedLanguage

data class SafetySheetUiState(
    val fileChooserVisible: Boolean = false,
    val fileSaverVisible: Boolean = false,
    val pdfExportVisible: Boolean = false,
    val disclaimerConfirmed: Boolean,
    val darkMode: Boolean,
    val language: SupportedLanguage,
    val exportUrl: String
) {
    constructor(settings: Settings) : this(
        disclaimerConfirmed = settings.disclaimerConfirmed,
        darkMode = settings.darkTheme,
        language = settings.language,
        exportUrl = settings.exportUrl
    )
}