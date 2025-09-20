package io.github.chaosdave34.benzol.ui

import androidx.compose.animation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import benzol.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FloatingActionButtonMenu(
    visible: Boolean,
    viewModel: SafetySheetViewModel
) {
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = visible,
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
                    scope.launch {
                        viewModel.setDefaultInputValues()
                    }
                },
                text = stringResource(Res.string.new_file),
                imageVector = Icons.Filled.ClearAll,
                contentDescription = stringResource(Res.string.new_file)
            )
            FabButton(
                onClick = viewModel::openFileChooser,
                text = stringResource(Res.string.open_file),
                imageVector = Icons.Filled.FileOpen,
                contentDescription = stringResource(Res.string.new_file)
            )
            FabButton(
                onClick = viewModel::openFileSaver,
                text = stringResource(Res.string.save_file),
                imageVector = Icons.Filled.Save,
                contentDescription = stringResource(Res.string.save_file)
            )
            FabButton(
                onClick = viewModel::openPdfExport,
                text = stringResource(Res.string.export_file),
                imageVector = Icons.Filled.PictureAsPdf,
                contentDescription = stringResource(Res.string.export_file)
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