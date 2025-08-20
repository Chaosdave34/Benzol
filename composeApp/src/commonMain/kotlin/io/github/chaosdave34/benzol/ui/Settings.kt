package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.SupportedLanguage
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    viewModel: SafetySheetViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    var languageDropdownExpanded by remember { mutableStateOf(false) }

    if (uiState.settingsVisible) {
        Dialog(
            onDismissRequest = viewModel::closeSettings
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(10.dp).width(IntrinsicSize.Max),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(stringResource(Res.string.settings))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(
                            checked = uiState.darkMode,
                            onCheckedChange = viewModel::setDarkMode
                        )

                        Text(stringResource(Res.string.dark_theme))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = languageDropdownExpanded,
                            onExpandedChange = { languageDropdownExpanded = it }
                        ) {
                            TextField(
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                value = stringResource(uiState.language.resource),
                                readOnly = true,
                                onValueChange = {},
                                label = { Text(stringResource(Res.string.search_option)) },
                            )

                            ExposedDropdownMenu(
                                expanded = languageDropdownExpanded,
                                onDismissRequest = { languageDropdownExpanded = false }
                            ) {
                                SupportedLanguage.entries.forEach {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(it.resource)) },
                                        onClick = {
                                            languageDropdownExpanded = false
                                            viewModel.setLanguage(it)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = viewModel::closeSettings
                        ) {
                            Icon(Icons.Rounded.Close, stringResource(Res.string.close))
                        }
                    }
                }
            }
        }
    }
}