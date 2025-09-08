package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.export_file
import benzol.composeapp.generated.resources.open_file
import benzol.composeapp.generated.resources.save_file
import io.github.chaosdave34.benzol.files.HtmlFile
import io.github.chaosdave34.benzol.files.saveAsPdf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.util.prefs.Preferences

const val LAST_USED_FOLDER_OPEN = "last_used_folder_open"
const val LAST_USED_FOLDER_SAVE = "last_used_folder_save"
const val LAST_USED_FOLDER_EXPORT = "last_used_folder_export"

private val preferences: Preferences = Preferences.userRoot().node("io.github.chaosdave34.benzol")

@Composable
actual fun FileChooser(
    coroutineScope: CoroutineScope,
    result: (String?, String) -> Unit,
    onClose: () -> Unit
) {
    val title = stringResource(Res.string.open_file)
    AwtWindow(
        create = {
            object : FileDialog(null as Frame?, title, LOAD) {
                init {
                    directory = preferences.get(LAST_USED_FOLDER_OPEN, File(System.getProperty("user.home")).absolutePath)
                }

                override fun setVisible(visible: Boolean) {
                    super.setVisible(visible)
                    if (visible) {
                        if (directory != null && file != null) {
                            coroutineScope.launch(Dispatchers.IO) {
                                result(File(directory + file).readText(), file)
                                onClose()
                            }
                            preferences.put(LAST_USED_FOLDER_OPEN, directory)
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
    output: () -> Pair<String, String>,
    onClose: () -> Unit
) {
    val output = output()

    val title = stringResource(Res.string.save_file)
    AwtWindow(
        create = {
            object : FileDialog(null as Frame?, title, SAVE) {
                init {
                    directory = preferences.get(LAST_USED_FOLDER_SAVE, File(System.getProperty("user.home")).absolutePath)
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
                            preferences.put(LAST_USED_FOLDER_SAVE, directory)
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
    output: () -> Pair<HtmlFile, String>,
    onClose: () -> Unit
) {
    val output = output()

    val title = stringResource(Res.string.export_file)
    AwtWindow(
        create = {
            object : FileDialog(null as Frame?, title, SAVE) {
                init {
                    directory = preferences.get(LAST_USED_FOLDER_EXPORT, File(System.getProperty("user.home")).absolutePath)
                    file = output.second
                }

                override fun setVisible(visible: Boolean) {
                    super.setVisible(visible)
                    if (visible) {
                        if (directory != null && file != null) {
                            coroutineScope.launch(Dispatchers.IO) {
                                val html = output.first
                                saveAsPdf(html.create(), directory, file)
                                onClose()
                            }
                            preferences.put(LAST_USED_FOLDER_EXPORT, directory)
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

