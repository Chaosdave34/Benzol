package io.github.chaosdave34.benzol.ui

import androidx.compose.animation.*
import androidx.compose.material3.*
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.ExportFileFabButton
import io.github.chaosdave34.benzol.SaveFileFabButton
import io.github.chaosdave34.benzol.rememberFilePicker
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

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
                    val drawableResource by remember {
                        derivedStateOf {
                            if (checkedProgress > 0.5f) Res.drawable.close else Res.drawable.add
                        }
                    }

                    Icon(
                        painter = rememberVectorPainter(vectorResource(drawableResource)),
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
                imageVector = vectorResource(Res.drawable.clear_all),
                contentDescription = stringResource(Res.string.new_file)
            )
            FabButton(
                onClick = {
                    filePickerLauncher.launch()
                    expanded = false
                },
                text = stringResource(Res.string.open_file),
                imageVector = vectorResource(Res.drawable.file_open_filled),
                contentDescription = stringResource(Res.string.new_file)
            )
            SaveFileFabButton(
                inputState = inputState,
                onClick = {
                    expanded = false
                }
            )
            ExportFileFabButton(
                onClick = {
                    expanded = false
                }
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