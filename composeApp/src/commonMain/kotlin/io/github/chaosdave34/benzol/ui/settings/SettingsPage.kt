package io.github.chaosdave34.benzol.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.SupportedLanguage
import io.github.chaosdave34.benzol.settings.Theme
import io.github.chaosdave34.benzol.ui.AppPageBox
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import io.github.chaosdave34.benzol.ui.Section
import io.ktor.util.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsPage(
    viewModel: SafetySheetViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    AppPageBox(
        Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
        title = stringResource(Res.string.settings)
    ) { scrollState ->
        Column(
            Modifier
                .verticalScroll(scrollState)
                .width(IntrinsicSize.Min),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Section(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ThemeSetting(
                    theme = uiState.theme,
                    onThemeChange = viewModel::setTheme
                )
            }

            Section(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LanguageSetting(
                    language = uiState.language,
                    onLanguageChange = viewModel::setLanguage
                )
            }

            if (PlatformUtils.IS_BROWSER) {
                Section {
                    ExportUrlSetting(
                        exportUrl = uiState.exportUrl,
                        onExportUrlChange = viewModel::setExportUrl
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSetting(
    theme: Theme,
    onThemeChange: (Theme) -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = { dropdownExpanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            value = stringResource(theme.label),
            readOnly = true,
            onValueChange = {},
            label = { Text(stringResource(Res.string.theme)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded) }
        )

        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
        ) {
            Theme.entries.forEach {
                DropdownMenuItem(
                    text = { Text(stringResource(it.label)) },
                    onClick = {
                        dropdownExpanded = false
                        onThemeChange(it)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSetting(
    language: SupportedLanguage,
    onLanguageChange: (SupportedLanguage) -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = { dropdownExpanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            value = stringResource(language.label),
            readOnly = true,
            onValueChange = {},
            label = { Text(stringResource(Res.string.language)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded) }
        )

        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
        ) {
            SupportedLanguage.entries.forEach {
                DropdownMenuItem(
                    text = { Text(stringResource(it.label)) },
                    onClick = {
                        dropdownExpanded = false
                        onLanguageChange(it)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
private fun ExportUrlSetting(
    exportUrl: String,
    onExportUrlChange: (String) -> Unit
) {
    OutlinedTextField(
        value = exportUrl,
        onValueChange = onExportUrlChange,
        singleLine = true,
        label = {
            Text(stringResource(Res.string.export_url))
        },
    )
}