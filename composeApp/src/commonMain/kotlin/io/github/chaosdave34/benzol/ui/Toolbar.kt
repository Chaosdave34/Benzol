package io.github.chaosdave34.benzol.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.*
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.ExportFileIconButton
import io.github.chaosdave34.benzol.SaveFileIconButton
import io.github.chaosdave34.benzol.rememberFilePicker
import org.jetbrains.compose.resources.stringResource

context(viewModel: SafetySheetViewModel)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BoxScope.Toolbar() {
    val inputState by viewModel.inputState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val filePickerLauncher = rememberFilePicker()

    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .offset(x = -ScreenOffset),
        visible = uiState.fabOrToolbarVisible,
        enter = slideInHorizontally { fullWidth ->
            fullWidth + ScreenOffset.value.toInt()
        } + fadeIn(),
        exit = slideOutHorizontally { fullWidth ->
            fullWidth + ScreenOffset.value.toInt()
        } + fadeOut()
    ) {
        VerticalFloatingToolbar(
            expanded = false,
            colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
        ) {
            ToolbarButton(
                onClick = viewModel::resetInput,
                tooltip = stringResource(Res.string.new_file),
                imageVector = Icons.Filled.ClearAll,
                contentDescription = stringResource(Res.string.new_file)
            )
            ToolbarButton(
                onClick = filePickerLauncher::launch,
                tooltip = stringResource(Res.string.open_file),
                imageVector = Icons.Filled.FileOpen,
                contentDescription = stringResource(Res.string.new_file)
            )
            ToolbarButton(
                tooltip = stringResource(Res.string.save_file),
                button = { SaveFileIconButton(inputState) }
            )
            ToolbarButton(
                tooltip = stringResource(Res.string.export_file),
                button = { ExportFileIconButton(viewModel.settings.exportUrl, inputState) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolbarButton(
    tooltip: String,
    button: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip {
                Text(tooltip)
            }
        },
        state = rememberTooltipState()
    ) {
        button()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolbarButton(
    onClick: () -> Unit,
    tooltip: String,
    imageVector: ImageVector,
    contentDescription: String,
) {
    ToolbarButton(
        tooltip = tooltip
    ) {
        IconButton(
            onClick = onClick
        ) {
            Icon(imageVector, contentDescription = contentDescription)
        }
    }
}