package io.github.chaosdave34.benzol.data

import io.github.chaosdave34.benzol.SupportedLanguage
import io.github.chaosdave34.benzol.settings.Settings
import io.github.chaosdave34.benzol.settings.Theme

data class SafetySheetUiState(
    val fileChooserVisible: Boolean = false,
    val fileSaverVisible: Boolean = false,
    val pdfExportVisible: Boolean = false,
    val disclaimerConfirmed: Boolean,
    val theme: Theme,
    val language: SupportedLanguage,
    val exportUrl: String
) {
    constructor(settings: Settings) : this(
        disclaimerConfirmed = settings.disclaimerConfirmed,
        theme = settings.theme,
        language = settings.language,
        exportUrl = settings.exportUrl
    )
}