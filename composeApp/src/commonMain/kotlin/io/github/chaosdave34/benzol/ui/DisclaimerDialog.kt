package io.github.chaosdave34.benzol.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.accept
import benzol.composeapp.generated.resources.disclaimer
import benzol.composeapp.generated.resources.important
import org.jetbrains.compose.resources.stringResource

@Composable
fun DisclaimerDialog(
    visible: Boolean,
    onConfirmation: () -> Unit
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(Res.string.important)) },
            text = { Text(stringResource(Res.string.disclaimer)) },
            confirmButton = {
                TextButton(
                    onClick = onConfirmation
                ) {
                    Text(stringResource(Res.string.accept))
                }
            }
        )
    }
}