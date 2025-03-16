package io.github.chaosdave34.benzol.files

@JsName("html2pdf")
external class Html2pdf {
    fun setMargin(src: Int): Html2pdf
    fun set(options: JsAny): Html2pdf
    fun from(src: String, type: String): Html2pdf
    fun toPdf(): Html2pdf
    fun save(filename: String)
}

external fun createOpt(scale: Int): JsAny

actual fun htmlToPdf(html: String, directory: String, fileName: String) {
    Html2pdf().set(createOpt(5)).setMargin(16).from(html, "string").toPdf().save(fileName)
}