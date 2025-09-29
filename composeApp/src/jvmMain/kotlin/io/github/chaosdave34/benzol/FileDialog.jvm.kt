package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.AwtWindow
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.export_file
import benzol.composeapp.generated.resources.open_file
import benzol.composeapp.generated.resources.save_file
import io.github.chaosdave34.benzol.files.htmlToPdf
import io.github.chaosdave34.benzol.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
actual fun FileChooser(
    scope: CoroutineScope,
    settings: Settings,
    onSelect: (String?, String) -> Unit,
    onClose: () -> Unit
) {
    ComposeFileDialog(
        title = stringResource(Res.string.open_file),
        mode = FileDialog.LOAD,
        directory = settings.lastUsedFolderOpen,
        callback = { directory, file ->
            if (directory != null && file != null) {
                scope.launch(Dispatchers.IO) {
                    onSelect(File(directory + file).readText(), file)
                    onClose()
                }
                settings.lastUsedFolderOpen = directory
            } else {
                onClose()
            }
        }
    )
}

@Composable
actual fun FileSaver(
    scope: CoroutineScope,
    settings: Settings,
    fileName: String,
    output: () -> String,
    onClose: () -> Unit
) {
    ComposeFileDialog(
        title = stringResource(Res.string.save_file),
        mode = FileDialog.SAVE,
        directory = settings.lastUsedFolderSave,
        fileName = fileName,
        callback = { directory, file ->
            if (directory != null && file != null) {
                scope.launch(Dispatchers.IO) {
                    File(directory, file).writeText(output())
                    onClose()
                }
                settings.lastUsedFolderSave = directory
            } else {
                onClose()
            }

        }
    )
}

@Composable
actual fun PdfExport(
    scope: CoroutineScope,
    settings: Settings,
    fileName: String,
    html: suspend () -> String,
    onClose: (Boolean) -> Unit
) {
    ComposeFileDialog(
        title = stringResource(Res.string.export_file),
        mode = FileDialog.SAVE,
        directory = settings.lastUsedFolderExport,
        fileName = fileName,
        callback = { directory, file ->
            if (directory != null && file != null) {
                scope.launch(Dispatchers.IO) {
                    val byteArray = htmlToPdf(html())
                    File(directory, file).writeBytes(byteArray)
                    onClose(true)
                }
                settings.lastUsedFolderExport = directory
            } else {
                onClose(false)
            }
        }
    )
}

@Composable
fun ComposeFileDialog(
    title: String,
    mode: Int,
    directory: String?,
    fileName: String? = null,
    callback: (String?, String?) -> Unit
) {
    AwtWindow(
        create = {
            object : FileDialog(null as Frame?, title, mode) {
                init {
                    this.directory = directory ?: System.getProperty("user.home")
                    fileName?.let { this.file = it }
                }

                override fun setVisible(visible: Boolean) {
                    super.setVisible(visible)
                    if (visible) {
                        callback(this.directory, file)
                    }
                }
            }
        },
        dispose = FileDialog::dispose
    )
}



