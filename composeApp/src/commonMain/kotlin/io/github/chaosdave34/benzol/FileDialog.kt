package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import io.github.chaosdave34.benzol.files.HtmlFile
import kotlinx.coroutines.CoroutineScope

@Composable
expect fun FileOpener(
    coroutineScope: CoroutineScope,
    result: (String?, String) -> Unit,
    onClose: () -> Unit
)

@Composable
expect fun FileSaver(
    coroutineScope: CoroutineScope,
    fileName: String,
    output: () -> String,
    onClose: () -> Unit,
)

@Composable
expect fun PdfExport(
    coroutineScope: CoroutineScope,
    fileName: String,
    output: () -> HtmlFile,
    onClose: () -> Unit
)