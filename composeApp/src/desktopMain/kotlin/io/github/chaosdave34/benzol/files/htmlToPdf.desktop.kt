package io.github.chaosdave34.benzol.files

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.FileOutputStream

actual fun htmlToPdf(html: String, directory: String, fileName: String) {
    val builder = PdfRendererBuilder()
    builder.useFastMode()
    builder.withHtmlContent(html, "")
    builder.toStream(FileOutputStream(directory + fileName))
    builder.run()
}