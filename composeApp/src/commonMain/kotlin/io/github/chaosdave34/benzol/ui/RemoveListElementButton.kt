package io.github.chaosdave34.benzol.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.stringResource

@Composable
fun RemoveListElementButton(
    list: SnapshotStateList<*>,
    index: Int
) {
    Button(
        onClick = { if (index >= 0 && index <= list.lastIndex) list.removeAt(index) },
    ) { Icon(Icons.Rounded.Delete, stringResource(Res.string.delete)) }
}