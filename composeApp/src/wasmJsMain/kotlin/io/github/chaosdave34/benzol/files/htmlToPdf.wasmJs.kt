package io.github.chaosdave34.benzol.files

@JsModule("html2pdf.js")
external class html2pdf {
    fun setMargin(src: Int): html2pdf
    fun set(options: JsAny): html2pdf
    fun from(src: String, type: String): html2pdf
    fun toPdf(): html2pdf
    fun save(filename: String)
}

external fun createOpt(scale: Int): JsAny

actual fun htmlToPdf(html: String, directory: String, fileName: String) {
    html2pdf().set(createOpt(4)).setMargin(16).from(html, "string").toPdf().save(fileName)
}

