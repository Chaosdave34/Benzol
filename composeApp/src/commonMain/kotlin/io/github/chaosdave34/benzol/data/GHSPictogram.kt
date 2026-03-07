package io.github.chaosdave34.benzol.data

import benzol.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import kotlin.io.encoding.Base64

enum class GHSPictogram(val drawableResource: DrawableResource) {
    GHS_001(Res.drawable.ghs_001),
    GHS_002(Res.drawable.ghs_002),
    GHS_003(Res.drawable.ghs_003),
    GHS_004(Res.drawable.ghs_004),
    GHS_005(Res.drawable.ghs_005),
    GHS_006(Res.drawable.ghs_006),
    GHS_007(Res.drawable.ghs_007),
    GHS_008(Res.drawable.ghs_008),
    GHS_009(Res.drawable.ghs_009);

    var base64String: String = ""

    val alt = name.lowercase().replace("_0", "")

    companion object {
        suspend fun setBase64() {
            entries.forEach {
                val byteArray = Res.readBytes("drawable/${it.name.lowercase()}.png")
                it.base64String = Base64.encode(byteArray)
            }
        }

        fun fromId(id: String) = entries.find { it.alt == id }
    }
}