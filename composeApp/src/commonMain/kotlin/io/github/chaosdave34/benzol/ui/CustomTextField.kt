package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    supportingText: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        singleLine = true,
        supportingText = supportingText,
        suffix = suffix,
        readOnly = readOnly,
        isError = isError,
        keyboardOptions = keyboardOptions
    )
}