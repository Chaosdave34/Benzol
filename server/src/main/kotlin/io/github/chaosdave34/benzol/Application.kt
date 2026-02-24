package io.github.chaosdave34.benzol

import io.github.chaosdave34.benzol.files.htmlToPdf
import io.github.chaosdave34.benzol.files.setupLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    setupLogging()
    EngineMain.main(args)
}

fun Application.module() {
    val allowAnyHost = environment.config.property("cors.allowAnyHost").getAs<Boolean>()
    val allowedHosts = environment.config.property("cors.allowedHosts").getAs<String>().split(", *".toRegex())

    log.info("AllowAnyHost: $allowAnyHost")
    log.info("AllowedHost: $allowedHosts")

    install(CORS) {
        allowMethod(HttpMethod.Post)

        if (allowAnyHost) anyHost()
        allowedHosts.forEach(::allowHost)
    }
    routing {
        post("/export") {
            val html = call.receiveText()
            val pdf = try {
                htmlToPdf(html)
            } catch (_: Exception) {
                call.respond(HttpStatusCode.InternalServerError)
                return@post
            }
            call.respondBytes(pdf, ContentType.Application.Pdf)
        }
    }
}