package io.github.chaosdave34.benzol

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.logo
import benzol.composeapp.generated.resources.title
import io.github.chaosdave34.benzol.files.setupLogging
import io.github.vinceglb.filekit.FileKit
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() {
    setupLogging()
    FileKit.init(appId = "Benzol")

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.title),
            icon = painterResource(Res.drawable.logo)
        ) {
            App()
        }
    }
}