package io.github.chaosdave34.benzol.files

@JsName("html3pdf")
external class Html2pdf {
    fun setMargin(src: Int): Html2pdf
    fun set(options: JsAny): Html2pdf
    fun from(src: String, type: String): Html2pdf
    fun toPdf(): Html2pdf
    fun save(filename: String)
}

private val options: JsAny = js(
    """{html2canvas: {scale: 5, letterRendering: true}}"""
)

actual fun saveAsPdf(html: String, directory: String, fileName: String) {
    Html2pdf().set(options).setMargin(16).from(html, "string").toPdf().save(fileName)
}