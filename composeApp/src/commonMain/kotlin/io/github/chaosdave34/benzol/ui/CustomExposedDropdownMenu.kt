package io.github.chaosdave34.benzol.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.github.chaosdave34.benzol.data.Labeled
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Labeled> CustomExposedDropdownMenu(
    modifier: Modifier = Modifier,
    entries: Iterable<T>,
    label: String,
    selected: T,
    onSelectedChange: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            value = stringResource(selected.label),
            onValueChange = {},
            label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            entries.forEach {
                DropdownMenuItem(
                    text = { Text(stringResource(it.label)) },
                    onClick = {
                        expanded = false
                        onSelectedChange(it)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}