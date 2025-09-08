package io.github.chaosdave34.benzol

import io.github.chaosdave34.benzol.files.htmlToPdf
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    routing {
        post("/export") {
            val html = call.receiveText()
            val pdf = htmlToPdf(html)
            call.respondBytes(pdf, ContentType.Application.Pdf)
        }
    }
}