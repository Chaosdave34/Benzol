package io.github.chaosdave34.benzol.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.about
import benzol.composeapp.generated.resources.disclaimer
import benzol.composeapp.generated.resources.github
import io.github.chaosdave34.benzol.ui.AppPageBox
import io.github.chaosdave34.benzol.ui.Section
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutPage() {
    val uriHandler = LocalUriHandler.current

    AppPageBox(
        Modifier.fillMaxWidth(),
        title = stringResource(Res.string.about),
        contentAlignment = Alignment.TopCenter,
    ) { scrollState ->
        Column(
            Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth(0.5f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Section(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.disclaimer)
                )
            }

            Section(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        uriHandler.openUri("https://github.com/Chaosdave34/Benzol")
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        painter = painterResource(Res.drawable.github),
                        contentDescription = stringResource(Res.string.github)
                    )
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(stringResource(Res.string.github))
                }
            }
            Section(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Version 1.4.1")
            }
        }
    }
}