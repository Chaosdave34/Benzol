package io.github.chaosdave34.benzol

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

actual fun getHttpClient(): HttpClient {
    return HttpClient(Js) {
        install(ContentNegotiation) {
            json()
        }
    }
}