package io.github.chaosdave34.benzol.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Input(
    modifier: Modifier = Modifier.fillMaxWidth(),
    value: String,
    onValueChange: (String) -> Unit,
    label: StringResource,
    supportingText: @Composable (() -> Unit)? = null
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        singleLine = true,
        supportingText = supportingText
    )
}
