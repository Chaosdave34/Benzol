package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.Substance
import io.github.chaosdave34.benzol.search.Source
import io.github.chaosdave34.benzol.ui.FormattedMolecularFormula
import io.github.chaosdave34.benzol.ui.adaptive.AdaptiveButton
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
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

                        FormattedMolecularFormula(formula = substance.molecularFormula)
                    }
                },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdaptiveButton(
                            icon = vectorResource(Res.drawable.topic_filled),
                            label = stringResource(substance.source.first.label),
                            onClick = {
                                uriHandler.openUri(substance.source.second)
                            },
                            enabled = substance.source.first != Source.Custom
                        )
                        FilledIconButton(
                            onClick = { onRemove(index) },
                        ) {
                            Icon(vectorResource(Res.drawable.delete_filled), contentDescription = stringResource(Res.string.delete))
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
                                imageVector = vectorResource(Res.drawable.drag_indicator),
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        }
    }
}
