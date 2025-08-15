package io.github.chaosdave34.benzol.files

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import com.openhtmltopdf.util.XRLog
import java.io.FileOutputStream

actual fun htmlToPdf(html: String, directory: String, fileName: String) {
    XRLog.listRegisteredLoggers().forEach { XRLog.setLevel(it, java.util.logging.Level.WARNING) }

    val builder = PdfRendererBuilder()
    builder.withHtmlContent(html, "")
    builder.toStream(FileOutputStream(directory + fileName))
    builder.run()
}