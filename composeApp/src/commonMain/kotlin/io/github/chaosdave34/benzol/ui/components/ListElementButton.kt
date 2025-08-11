package io.github.chaosdave34.benzol.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.add
import benzol.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> AddListElementButton(
    list: SnapshotStateList<T>,
    element: T
) {
    Button(
        onClick = { list.add(element) },
    ) { Icon(Icons.Rounded.Add, stringResource(Res.string.add)) }
}

@Composable
fun RemoveListElementButton(
    list: SnapshotStateList<*>,
    index: Int
) {
    Button(
        onClick = { if (index >= 0 && index <= list.lastIndex) list.removeAt(index) },
    ) { Icon(Icons.Rounded.Delete, stringResource(Res.string.delete)) }
}