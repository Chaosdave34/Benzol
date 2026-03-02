package io.github.chaosdave34.benzol.data

import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.danger
import benzol.composeapp.generated.resources.none_dash
import benzol.composeapp.generated.resources.warning
import org.jetbrains.compose.resources.StringResource

enum class SignalWord(override val label: StringResource, val internalLabel: String) : Labeled {
    NONE(Res.string.none_dash, ""),
    WARNING(Res.string.warning, "Achunt"),
    DANGER(Res.string.danger, "Gefahr");

    companion object {
        fun fromLabel(label: String): SignalWord {
            return entries.find { it.internalLabel == label } ?: NONE
        }
    }
}