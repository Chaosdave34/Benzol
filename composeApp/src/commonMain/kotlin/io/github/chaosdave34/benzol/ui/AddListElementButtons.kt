package io.github.chaosdave34.benzol.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.add
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddListElementButton(
    onAdd: () -> Unit,
) {
    FilledIconButton(
        onClick = onAdd,
    ) {
        Icon(Icons.Filled.Add, stringResource(Res.string.add))
    }
}
