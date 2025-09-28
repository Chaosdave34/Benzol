package io.github.chaosdave34.benzol.data

import benzol.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import kotlin.io.encoding.Base64

enum class GHSPictogram(val drawableResource: DrawableResource, private val fileName: String, val alt: String) {
    Explosion(Res.drawable.explosion, "explosion", "ghs01"),
    Flame(Res.drawable.flame, "flame", "ghs02"),
    FlameOverCircle(Res.drawable.flame_over_circle, "flame_over_circle", "ghs03"),
    GasBottle(Res.drawable.gas_bottle, "gas_bottle", "ghs04"),
    Acid(Res.drawable.acid, "acid", "ghs05"),
    Skull(Res.drawable.skull, "skull", "ghs06"),
    Exclamation(Res.drawable.exclamation, "exclamation", "ghs07"),
    Silhouette(Res.drawable.silhouette, "silhouette", "ghs08"),
    Nature(Res.drawable.nature, "nature", "ghs09");

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
                "ghs01" -> Explosion
                "ghs02" -> Flame
                "ghs03" -> FlameOverCircle
                "ghs04" -> GasBottle
                "ghs05" -> Acid
                "ghs06" -> Skull
                "ghs07" -> Exclamation
                "ghs08" -> Silhouette
                "ghs09" -> Nature
                else -> null
            }
        }
    }
}