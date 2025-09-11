package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.down
import benzol.composeapp.generated.resources.up
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> MoveUpAndDown(
    list: SnapshotStateList<T>,
    index: Int,
    padding: Dp = 0.dp
) {
    Column(
        modifier = Modifier.fillMaxHeight().padding(vertical = padding),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (index != 0) {
            Button(
                onClick = {
                    if (index < list.lastIndex) {
                        list[index] = list.set(index + 1, list[index])
                    }
                },
                imageVector = Icons.Rounded.KeyboardArrowUp,
                contentDescription = stringResource(Res.string.up),
            )
        } else {
            Spacer(Modifier.size(IconButtonDefaults.extraSmallContainerSize()))
        }
        if (index != list.lastIndex) {
            Button(
                onClick = {
                    if (index < list.lastIndex) {
                        list[index] = list.set(index + 1, list[index])
                    }
                },
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(Res.string.down)
            )
        } else {
            Spacer(Modifier.size(IconButtonDefaults.extraSmallContainerSize()))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Button(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String
) {
    IconButton(
        modifier = Modifier.size(
            IconButtonDefaults.extraSmallContainerSize()
        ),
        onClick = onClick,
        shape = IconButtonDefaults.extraSmallRoundShape,
    ) {
        Icon(
            modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize),
            imageVector = imageVector,
            contentDescription = contentDescription,
        )
    }
}
