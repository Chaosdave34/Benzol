package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.AwtWindow
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.export_file
import benzol.composeapp.generated.resources.open_file
import benzol.composeapp.generated.resources.save_file
import io.github.chaosdave34.benzol.files.HtmlFile
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
    coroutineScope: CoroutineScope,
    settings: Settings,
    result: (String?, String) -> Unit,
    onClose: () -> Unit
) {
    val title = stringResource(Res.string.open_file)
    AwtWindow(
        create = {
            object : FileDialog(null as Frame?, title, LOAD) {
                init {
                    directory = settings.lastUsedFolderOpen ?: System.getProperty("user.home")
                }

                override fun setVisible(visible: Boolean) {
                    super.setVisible(visible)
                    if (visible) {
                        if (directory != null && file != null) {
                            coroutineScope.launch(Dispatchers.IO) {
                                result(File(directory + file).readText(), file)
                                onClose()
                            }
                            settings.lastUsedFolderOpen = directory
                        } else {
                            onClose()
                        }
                    }
                }
            }
        },
        dispose = FileDialog::dispose,
    )
}

@Composable
actual fun FileSaver(
    coroutineScope: CoroutineScope,
    settings: Settings,
    output: () -> Pair<String, String>,
    onClose: () -> Unit
) {
    val output = output()

    val title = stringResource(Res.string.save_file)
    AwtWindow(
        create = {
            object : FileDialog(null as Frame?, title, SAVE) {
                init {
                    directory = settings.lastUsedFolderSave ?: System.getProperty("user.home")
                    file = output.second
                }

                override fun setVisible(visible: Boolean) {
                    super.setVisible(visible)
                    if (visible) {
                        if (directory != null && file != null) {
                            coroutineScope.launch(Dispatchers.IO) {
                                File(directory, file).writeText(output.first)
                                onClose()
                            }
                            settings.lastUsedFolderSave = directory
                        } else {
                            onClose()
                        }
                    }
                }
            }
        },
        dispose = FileDialog::dispose
    )
}

@Composable
actual fun PdfExport(
    coroutineScope: CoroutineScope,
    settings: Settings,
    output: () -> Pair<HtmlFile, String>,
    onClose: (Boolean) -> Unit
) {
    val output = output()

    val title = stringResource(Res.string.export_file)
    AwtWindow(
        create = {
            object : FileDialog(null as Frame?, title, SAVE) {
                init {
                    directory = settings.lastUsedFolderExport ?: System.getProperty("user.home")
                    file = output.second
                }

                override fun setVisible(visible: Boolean) {
                    super.setVisible(visible)
                    if (visible) {
                        if (directory != null && file != null) {
                            coroutineScope.launch(Dispatchers.IO) {
                                val html = output.first
                                val byteArray = htmlToPdf(html.create())
                                File(directory, file).writeBytes(byteArray)
                                onClose(true)
                            }
                            settings.lastUsedFolderExport = directory
                        } else {
                            onClose(false)
                        }
                    }
                }
            }
        },
        dispose = FileDialog::dispose
    )
}

