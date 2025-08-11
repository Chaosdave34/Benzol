package io.github.chaosdave34.benzol

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Benzol",
        icon = painterResource(Res.drawable.logo)
    ) {
        App()
    }
}