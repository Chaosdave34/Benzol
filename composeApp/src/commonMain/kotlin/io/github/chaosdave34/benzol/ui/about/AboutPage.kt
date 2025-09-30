package io.github.chaosdave34.benzol.ui.about

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.disclaimer
import benzol.composeapp.generated.resources.github
import io.github.chaosdave34.benzol.ui.AppPageBox
import io.github.chaosdave34.benzol.ui.CustomCard
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import io.github.chaosdave34.benzol.ui.adaptive.AdaptivePageColumn
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

context(viewModel: SafetySheetViewModel)
@Composable
fun AboutPage() {
    val uriHandler = LocalUriHandler.current

    AppPageBox(
        Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) { scrollState ->
        AdaptivePageColumn(
            scrollState = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomCard(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.disclaimer)
                )
            }

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


            Text("Version 2.1.1")
        }
    }
}