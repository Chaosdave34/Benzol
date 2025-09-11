package io.github.chaosdave34.benzol.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import benzol.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BoxScope.Toolbar(
    visible: Boolean,
    viewModel: SafetySheetViewModel
) {
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .offset(x = -ScreenOffset),
        visible = visible,
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
                onClick = {
                    viewModel.resetInput()
                    scope.launch {
                        viewModel.setDefaultInputValues()
                    }
                },
                tooltip = stringResource(Res.string.new_file),
                imageVector = Icons.Filled.ClearAll,
                contentDescription = stringResource(Res.string.new_file)
            )
            ToolbarButton(
                onClick = viewModel::openFileChooser,
                tooltip = stringResource(Res.string.open_file),
                imageVector = Icons.Filled.FileOpen,
                contentDescription = stringResource(Res.string.new_file)
            )
            ToolbarButton(
                onClick = viewModel::openFileSaver,
                tooltip = stringResource(Res.string.save),
                imageVector = Icons.Filled.Save,
                contentDescription = stringResource(Res.string.save)
            )
            ToolbarButton(
                onClick = viewModel::openPdfExport,
                tooltip = stringResource(Res.string.export_file),
                imageVector = Icons.Filled.PictureAsPdf,
                contentDescription = stringResource(Res.string.export_file)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolbarButton(
    onClick: () -> Unit,
    tooltip: String,
    imageVector: ImageVector,
    contentDescription: String
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(tooltip)
            }
        },
        state = rememberTooltipState()
    ) {
        FilledIconButton(
            onClick = onClick
        ) {
            Icon(imageVector, contentDescription = contentDescription)
        }
    }
}