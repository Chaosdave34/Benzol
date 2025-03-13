package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import io.github.chaosdave34.benzol.files.HtmlFile
import io.github.chaosdave34.benzol.files.htmlToPdf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Dialog
import java.awt.FileDialog
import java.io.File
import java.util.prefs.Preferences

const val LAST_USED_FOLDER_OPEN = "last_used_folder_open"
const val LAST_USED_FOLDER_SAVE = "last_used_folder_save"
const val LAST_USED_FOLDER_EXPORT = "last_used_folder_export"

private val preferences: Preferences = Preferences.userRoot().node("io.github.chaosdave34.benzol")

@Composable
actual fun FileOpener(
    coroutineScope: CoroutineScope,
    result: (String?, String) -> Unit,
    onClose: () -> Unit
) {
    val dialog: Dialog? = null
    AwtWindow(
        create = {
            object : FileDialog(dialog, "", LOAD) {
                init {
                    isMultipleMode = false
                    directory = preferences.get(LAST_USED_FOLDER_OPEN, File(System.getProperty("user.home")).absolutePath)
                }

                override fun setVisible(visible: Boolean) {
                    super.setVisible(visible)
                    if (visible) {
                        if (directory != null && file != null) {
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    result(File(directory + file).readText(), file)
                                    onClose()
                                }
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
    fileName: String,
    output: () -> String,
    onClose: () -> Unit
) {
    val fileName = if (fileName.isEmpty()) "Unbenannt.cb2" else "$fileName.cb2"

    val dialog: Dialog? = null
    AwtWindow(
        create = {
            object : FileDialog(dialog, "", SAVE) {
                init {
                    isMultipleMode = false
                    directory = preferences.get(LAST_USED_FOLDER_SAVE, File(System.getProperty("user.home")).absolutePath)
                    file = fileName
                }

                override fun setVisible(visible: Boolean) {
                    super.setVisible(visible)
                    if (visible) {
                        if (directory != null && file != null) {
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    File(directory, file).writeText(output())
                                    onClose()
                                }
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
actual fun PdfExport(coroutineScope: CoroutineScope, fileName: String, output: () -> HtmlFile, onClose: () -> Unit) {
    val fileName = if (fileName.isEmpty()) "Unbenannt.pdf" else "$fileName.pdf"

    val dialog: Dialog? = null
    AwtWindow(
        create = {
            object : FileDialog(dialog, "", SAVE) {
                init {
                    isMultipleMode = false
                    directory = preferences.get(LAST_USED_FOLDER_EXPORT, File(System.getProperty("user.home")).absolutePath)
                    file = fileName
                }

                override fun setVisible(visible: Boolean) {
                    super.setVisible(visible)
                    if (visible) {
                        if (directory != null && file != null) {
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    val html = output.invoke()
                                    htmlToPdf(html.create(), directory, file)
                                    onClose()
                                }
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

