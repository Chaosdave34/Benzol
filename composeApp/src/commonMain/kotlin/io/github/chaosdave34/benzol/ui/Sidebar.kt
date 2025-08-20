package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Sidebar(
    viewModel: SafetySheetViewModel,
    resetInput: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    var dropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        tonalElevation = 8.dp
    ) {
        Column(
            Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                Button(
                    onClick = { dropdownExpanded = true }
                ) {
                    Icon(Icons.Rounded.Menu, stringResource(Res.string.open_settings))
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.new_file)) },
                        onClick = {
                            dropdownExpanded = false
                            resetInput()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.open_file)) },
                        onClick = {
                            dropdownExpanded = false
                            viewModel.openFileChooser()
                        }
                    )
                    DropdownMenuItem(
                        {
                            Text(stringResource(Res.string.save_file))
                        },
                        onClick = {
                            dropdownExpanded = false
                            viewModel.openFileSaver()
                        }
                    )
                    DropdownMenuItem(
                        { Text(stringResource(Res.string.export_file)) },
                        onClick = {
                            dropdownExpanded = false
                            viewModel.openPdfExport()
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        { Text(stringResource(Res.string.settings)) },
                        onClick = {
                            dropdownExpanded = false
                            viewModel.openSettings()
                        }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    uriHandler.openUri("https://github.com/Chaosdave34/Benzol")
                }
            ) {
                Image(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    painter = painterResource(Res.drawable.github),
                    contentDescription = stringResource(Res.string.github)
                )
            }
            Text("1.4.1")
        }
    }
}