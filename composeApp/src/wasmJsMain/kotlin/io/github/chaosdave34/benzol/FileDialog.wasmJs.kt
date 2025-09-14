package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import io.github.chaosdave34.benzol.files.HtmlFile
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.khronos.webgl.Int8Array
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.url.URL
import org.w3c.files.File
import org.w3c.files.FileReader

private val client = HttpClient {
    install(DefaultRequest) {
        url("http://localhost:8080/")
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun FileChooser(
    coroutineScope: CoroutineScope,
    result: (String?, String) -> Unit,
    onClose: () -> Unit
) {
    val input = document.createElement("input") as HTMLInputElement

    input.style.display = "none"

    document.body?.appendChild(input)

    input.apply {
        type = "file"
        multiple = false
    }

    input.onchange = { event ->
        val file = event.target?.unsafeCast<HTMLInputElement>()?.files?.item(0)

        if (file != null) {
            val reader = FileReader()
            reader.onload = { _ ->
                result(reader.result?.unsafeCast<JsString>()?.toString(), file.name)
            }

            reader.readAsText(file)
        }

        document.body?.removeChild(input)
        onClose()
    }

    input.oncancel = {
        document.body?.removeChild(input)
        onClose()
    }

    input.click()
}

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun FileSaver(
    coroutineScope: CoroutineScope,
    output: () -> Pair<String, String>,
    onClose: () -> Unit
) {
    coroutineScope.launch {
        val output = output()

        val jsArray = JsArray<JsAny?>()
        val byteArray = output.first.encodeToByteArray().map { it.toInt().toJsNumber() }.toJsArray()
        jsArray[0] = Int8Array(byteArray)

        val file = File(jsArray, output.second)
        val a = document.createElement("a") as HTMLAnchorElement
        a.href = URL.createObjectURL(file)
        a.download = output.second
        a.click()

        onClose()
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun PdfExport(
    coroutineScope: CoroutineScope,
    output: () -> Pair<HtmlFile, String>,
    onClose: () -> Unit
) {
    coroutineScope.launch {
        val output = output()
        val pdf = client.post("export") {
            setBody(output.first)
        }.bodyAsBytes()

        val jsArray = JsArray<JsAny?>()
        val byteArray = pdf.map { it.toInt().toJsNumber() }.toJsArray()
        jsArray[0] = Int8Array(byteArray)

        val file = File(jsArray, output.second)
        val a = document.createElement("a") as HTMLAnchorElement
        a.href = URL.createObjectURL(file)
        a.download = output.second
        a.click()

        onClose()
    }
}