package io.github.chaosdave34.benzol.data

import benzol.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

enum class Wgk(val label: StringResource, private val internalLabel: String) {
    NONE(Res.string.none_dash, ""),
    WGK_1(Res.string.wgk_1, "WGK 1"),
    WGK_2(Res.string.wgk_2, "WGK 2"),
    WGK_3(Res.string.wgk_3, "WGK 3");

    companion object {
        fun fromLabel(label: String): Wgk {
            return entries.find { it.internalLabel == label } ?: NONE
        }
    }
}