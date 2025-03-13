package io.github.chaosdave34.benzol

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.logo
import io.github.chaosdave34.benzol.ui.App
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Benzol",
        icon = painterResource(Res.drawable.logo)
    ) {
        App()
    }
}