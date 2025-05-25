package io.github.chaosdave34.benzol.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.add
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