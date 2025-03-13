package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.close
import benzol.composeapp.generated.resources.dark_theme
import benzol.composeapp.generated.resources.settings
import com.russhwolf.settings.set
import io.github.chaosdave34.benzol.getSettings
import org.jetbrains.compose.resources.stringResource

@Composable
fun Settings(open: MutableState<Boolean>, darkTheme: MutableState<Boolean>) {
    val settings = getSettings()

    Dialog(
        onDismissRequest = { open.value = false }
    ) {
        Card {
            Column(
                modifier = Modifier.padding(10.dp).width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(stringResource(Res.string.settings))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Checkbox(
                        checked = darkTheme.value,
                        onCheckedChange = {
                            darkTheme.value = it
                            settings["dark_theme"] = it
                        }
                    )

                    Text(stringResource(Res.string.dark_theme))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { open.value = false }
                    ) {
                        Icon(Icons.Rounded.Close, stringResource(Res.string.close))
                    }
                }
            }
        }
    }
}