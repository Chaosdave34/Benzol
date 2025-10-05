package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Source
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.delete
import io.github.chaosdave34.benzol.data.Substance
import io.github.chaosdave34.benzol.search.Source
import io.github.chaosdave34.benzol.ui.adaptive.AdaptiveButton
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SubstanceList(
    substances: List<Substance>,
    onSubstanceClick: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onDrag: (Int, Int) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    val itemHeights = remember(substances) { MutableList(substances.size) { 0 } }
    var currentDragged by remember { mutableIntStateOf(-1) }
    var yOffset by remember { mutableFloatStateOf(0f) }

    Column {
        substances.forEachIndexed { index, substance ->
            ListItem(
                modifier = Modifier
                    .clickable(
                        onClick = { onSubstanceClick(index) },
                        enabled = currentDragged != index
                    )
                    .offset { IntOffset(0, if (currentDragged == index) yOffset.roundToInt() else 0) }
                    .zIndex(if (currentDragged == index) 100f else 0f)
                    .onGloballyPositioned { coordinates ->
                        itemHeights[index] = coordinates.size.height
                    },
                headlineContent = {
                    Text(substance.name)
                },
                supportingContent = {
                    Column {
                        if (substance.casNumber.isNotBlank()) {
                            Text(substance.casNumber)
                        }

                        val formula = substance.formattedMolecularFormula
                        if (formula.isNotBlank()) {
                            FormattedMolecularFormula(formula = formula)
                        } else {
                            Text(substance.molecularFormula)
                        }
                    }
                },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdaptiveButton(
                            icon = Icons.Filled.Source,
                            label = stringResource(substance.source.first.label),
                            onClick = {
                                uriHandler.openUri(substance.source.second)
                            },
                            enabled = substance.source.first != Source.Custom
                        )
                        FilledIconButton(
                            onClick = { onRemove(index) },
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(Res.string.delete))
                        }
                        Surface(
                            onClick = {},
                            enabled = false,
                        ) {
                            Icon(
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope { awaitPointerEvent() }

                                        detectDragGestures(
                                            onDragStart = {
                                                currentDragged = index
                                            },
                                            onDragEnd = {
                                                currentDragged = -1
                                                yOffset = 0f
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                yOffset += dragAmount.y

                                                // Move up
                                                if (currentDragged != 0) {
                                                    if ((-yOffset).roundToInt() > itemHeights[currentDragged - 1]) {
                                                        onDrag(currentDragged, currentDragged - 1)
                                                        currentDragged -= 1
                                                        yOffset += itemHeights[currentDragged]
                                                    }
                                                }

                                                // Move down
                                                if (currentDragged != substances.lastIndex) {
                                                    if (yOffset.roundToInt() > itemHeights[currentDragged]) {
                                                        onDrag(currentDragged, currentDragged + 1)
                                                        currentDragged += 1
                                                        yOffset -= itemHeights[currentDragged]
                                                    }
                                                }
                                            }
                                        )
                                    },
                                imageVector = Icons.Default.DragIndicator,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        }
    }
}
