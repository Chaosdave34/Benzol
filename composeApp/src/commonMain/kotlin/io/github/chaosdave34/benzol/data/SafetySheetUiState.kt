package io.github.chaosdave34.benzol.data

import io.github.chaosdave34.benzol.SupportedLanguage
import io.github.chaosdave34.benzol.settings.Settings
import io.github.chaosdave34.benzol.settings.Theme
import io.github.chaosdave34.benzol.ui.Destination

data class SafetySheetUiState(
    val selectedDestination: Destination = Destination.Sheet,
    val fileChooserVisible: Boolean = false,
    val fileSaverVisible: Boolean = false,
    val pdfExportVisible: Boolean = false,
    val disclaimerConfirmed: Boolean,
    val theme: Theme,
    val language: SupportedLanguage,
    val exportUrl: String,
    val fabOrToolbarVisible: Boolean = false,
) {
    constructor(
        startDestination: Destination,
        settings: Settings
    ) : this(
        selectedDestination = startDestination,
        disclaimerConfirmed = settings.disclaimerConfirmed,
        theme = settings.theme,
        language = settings.language,
        exportUrl = settings.exportUrl
    )
}