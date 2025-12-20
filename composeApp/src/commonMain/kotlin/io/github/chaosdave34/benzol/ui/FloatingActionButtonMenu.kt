package io.github.chaosdave34.benzol.ui

import androidx.compose.animation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.*
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.new_file
import benzol.composeapp.generated.resources.open_file
import io.github.chaosdave34.benzol.ExportFileFabButton
import io.github.chaosdave34.benzol.SaveFileFabButton
import io.github.chaosdave34.benzol.rememberFilePicker
import org.jetbrains.compose.resources.stringResource

context(viewModel: SafetySheetViewModel)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FloatingActionButtonMenu() {
    val uiState by viewModel.uiState.collectAsState()
    val inputState by viewModel.inputState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val filePickerLauncher = rememberFilePicker()

    AnimatedVisibility(
        visible = uiState.fabOrToolbarVisible,
        enter = slideInVertically { fullWidth ->
            fullWidth + ScreenOffset.value.toInt()
        } + fadeIn(),
        exit = slideOutVertically { fullWidth ->
            fullWidth + ScreenOffset.value.toInt()
        } + fadeOut()
    ) {
        FloatingActionButtonMenu(
            expanded = expanded,
            button = {
                ToggleFloatingActionButton(
                    checked = expanded,
                    onCheckedChange = { expanded = it },
                ) {
                    val imageVector by remember {
                        derivedStateOf {
                            if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                        }
                    }

                    Icon(
                        painter = rememberVectorPainter(imageVector),
                        contentDescription = null,
                        modifier = Modifier.animateIcon({ checkedProgress }),
                    )
                }
            }
        ) {
            FabButton(
                onClick = {
                    viewModel.resetInput()
                    expanded = false
                },
                text = stringResource(Res.string.new_file),
                imageVector = Icons.Filled.ClearAll,
                contentDescription = stringResource(Res.string.new_file)
            )
            FabButton(
                onClick = {
                    filePickerLauncher.launch()
                    expanded = false
                },
                text = stringResource(Res.string.open_file),
                imageVector = Icons.Filled.FileOpen,
                contentDescription = stringResource(Res.string.new_file)
            )
            SaveFileFabButton(inputState)
            ExportFileFabButton(
                exportUrl = viewModel.settings.exportUrl,
                inputState = inputState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FloatingActionButtonMenuScope.FabButton(
    onClick: () -> Unit,
    text: String,
    imageVector: ImageVector,
    contentDescription: String
) {
    FloatingActionButtonMenuItem(
        onClick = onClick,
        icon = {
            Icon(imageVector, contentDescription = contentDescription)
        },
        text = {
            Text(text)
        }
    )
}