package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.down
import benzol.composeapp.generated.resources.up
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> MoveUpAndDown(
    list: SnapshotStateList<T>,
    index: Int
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom
    ) {
        if (index != 0) {
            Icon(
                modifier = Modifier.clip(CircleShape).clickable {
                    if (index > 0) {
                        list[index] = list.set(index - 1, list[index])
                    }
                },
                imageVector = Icons.Rounded.KeyboardArrowUp,
                contentDescription = stringResource(Res.string.up)
            )
        }
        if (index != list.lastIndex) {
            Icon(
                modifier = Modifier.clip(CircleShape).clickable {
                    if (index < list.lastIndex) {
                        list[index] = list.set(index + 1, list[index])
                    }
                },
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(Res.string.down)
            )
        }
    }
}