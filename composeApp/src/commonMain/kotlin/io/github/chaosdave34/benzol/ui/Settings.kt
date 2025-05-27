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
import com.russhwolf.settings.set
import io.github.chaosdave34.benzol.SupportedLanguage
import io.github.chaosdave34.benzol.getSettings
import io.ktor.util.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    open: MutableState<Boolean>,
    language: SupportedLanguage,
    setLanguage: (SupportedLanguage) -> Unit,
    darkTheme: MutableState<Boolean>
) {
    val settings = getSettings()

    var languageDropdownExpanded by remember { mutableStateOf(false) }

    if (open.value) {
        Dialog(
            onDismissRequest = { open.value = false }
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
                            checked = darkTheme.value,
                            onCheckedChange = {
                                darkTheme.value = it
                                settings["dark_theme"] = it
                            }
                        )

                        Text(stringResource(Res.string.dark_theme))
                    }

                    val languageLocked = PlatformUtils.IS_BROWSER

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = languageDropdownExpanded && !languageLocked,
                            onExpandedChange = { languageDropdownExpanded = it }
                        ) {
                            TextField(
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                value = stringResource(language.resource),
                                readOnly = true,
                                onValueChange = {},
                                label = { Text(stringResource(Res.string.search_option)) },
                                enabled = !languageLocked
                            )

                            ExposedDropdownMenu(
                                expanded = languageDropdownExpanded && !languageLocked,
                                onDismissRequest = { languageDropdownExpanded = false }
                            ) {
                                SupportedLanguage.entries.forEach {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(it.resource)) },
                                        onClick = {
                                            languageDropdownExpanded = false
                                            setLanguage(it)
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
                            onClick = { open.value = false }
                        ) {
                            Icon(Icons.Rounded.Close, stringResource(Res.string.close))
                        }
                    }
                }
            }
        }
    }
}