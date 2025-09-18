package io.github.chaosdave34.benzol.files

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import com.openhtmltopdf.util.XRLog
import java.io.ByteArrayOutputStream
import java.util.logging.Level

fun htmlToPdf(html: String): ByteArray {
    XRLog.listRegisteredLoggers().forEach { XRLog.setLevel(it, Level.WARNING) }

    val document = Ksoup.parse(html)
    document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)

    val outputStream = ByteArrayOutputStream()

    val builder = PdfRendererBuilder()
    builder.withHtmlContent(document.html(), "")
    builder.toStream(outputStream)
    builder.run()

    return outputStream.toByteArray()
}