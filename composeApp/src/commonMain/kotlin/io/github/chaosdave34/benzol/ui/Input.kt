package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Input(
    modifier: Modifier = Modifier.fillMaxWidth(),
    value: MutableState<String>,
    onChange: (String) -> Unit = { value.value = it },
    label: StringResource,
    supportingText: @Composable (() -> Unit)? = null
) {
    TextField(
        modifier = modifier,
        value = value.value,
        onValueChange = onChange,
        label = { Text(stringResource(label)) },
        singleLine = true,
        supportingText = supportingText
    )
}