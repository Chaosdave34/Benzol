package io.github.chaosdave34.benzol

import benzol.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import kotlin.io.encoding.Base64


enum class GHSPictogram(val drawableResource: DrawableResource, private val fileName: String, val alt: String) {
    EXPLOSION(Res.drawable.explosion, "explosion", "ghs01"),
    FLAME(Res.drawable.flame, "flame", "ghs02"),
    FLAME_OVER_CIRCLE(Res.drawable.flame_over_circle, "flame_over_circle", "ghs03"),
    GAS_BOTTLE(Res.drawable.gas_bottle, "gas_bottle", "ghs04"),
    ACID(Res.drawable.acid, "acid", "ghs05"),
    SKULL(Res.drawable.skull, "skull", "ghs06"),
    EXCLAMATION(Res.drawable.exclamation, "exclamation", "ghs07"),
    SILHOUETTE(Res.drawable.silhouette, "silhouette", "ghs08"),
    NATURE(Res.drawable.nature, "nature", "ghs09");

    var base64String: String = ""

    companion object {
        suspend fun setBase64() {
            entries.forEach {
                val byteArray = Res.readBytes("drawable/${it.fileName}.png")
                it.base64String = Base64.encode(byteArray)
            }
        }

        fun fromId(id: String): GHSPictogram? {
            return when (id) {
                "ghs01" -> EXPLOSION
                "ghs02" -> FLAME
                "ghs03" -> FLAME_OVER_CIRCLE
                "ghs04" -> GAS_BOTTLE
                "ghs05" -> ACID
                "ghs06" -> SKULL
                "ghs07" -> EXCLAMATION
                "ghs08" -> SILHOUETTE
                "ghs09" -> NATURE
                else -> null
            }
        }
    }
}