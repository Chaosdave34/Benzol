package io.github.chaosdave34.benzol.data

import kotlinx.serialization.Serializable

@Serializable
data class Modifiable<T>(
    val original: T,
    var modified: T? = null
) {
    val current: T
        get() = modified ?: original

}