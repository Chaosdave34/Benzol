package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.add
import benzol.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListInput(
    list: List<String>,
    onRemove: (Int) -> Unit,
    onValueChange: (Int, String) -> Unit,
    onAdd: () -> Unit,
    onDrag: (Int, Int) -> Unit
) {
    val itemHeights = remember(list) { MutableList(list.size) { 0 } }
    var currentDragged by remember { mutableIntStateOf(-1) }
    var yOffset by remember { mutableFloatStateOf(0f) }

    Column {
        list.forEachIndexed { index, element ->
            ListItem(
                modifier = Modifier
                    .offset { IntOffset(0, if (currentDragged == index) yOffset.roundToInt() else 0) }
                    .zIndex(if (currentDragged == index) 100f else 0f)
                    .onGloballyPositioned { coordinates ->
                        itemHeights[index] = coordinates.size.height
                    },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                leadingContent = {
                    Text("•")
                },
                trailingContent = {
                    Surface(
                        onClick = {},
                        enabled = false,
                        color = MaterialTheme.colorScheme.surfaceContainerLow
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
                                            if (currentDragged != list.lastIndex) {
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
                },
                headlineContent = {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = element,
                        onValueChange = { onValueChange(index, it) },
                        trailingIcon = {
                            IconButton(
                                onClick = { onRemove(index) },
                            ) {
                                Icon(Icons.Filled.Delete, stringResource(Res.string.delete))
                            }
                        }
                    )
                }
            )
        }
        FilledIconButton(
            onClick = onAdd,
        ) {
            Icon(Icons.Filled.Add, stringResource(Res.string.add))
        }
    }
}