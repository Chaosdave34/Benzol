package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.accept
import benzol.composeapp.generated.resources.disclaimer
import benzol.composeapp.generated.resources.important
import com.russhwolf.settings.set
import io.github.chaosdave34.benzol.getSettings
import org.jetbrains.compose.resources.stringResource

@Composable
fun Disclaimer() {
    val settings = getSettings()
    var checked by remember { mutableStateOf(settings.getBoolean("disclaimer", false)) }

    if (!checked) {
        Dialog(
            onDismissRequest = {}
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(10.dp).width(IntrinsicSize.Max)
                ) {
                    Text(stringResource(Res.string.important))

                    Text("")

                    Text(stringResource(Res.string.disclaimer))

                    Column(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalAlignment = Alignment.End
                    ) {
                        Button(
                            onClick = {
                                checked = true
                                settings["disclaimer"] = true
                            }
                        ) {
                            Icon(Icons.Rounded.Done, stringResource(Res.string.accept))
                        }
                    }
                }
            }
        }

    }
}