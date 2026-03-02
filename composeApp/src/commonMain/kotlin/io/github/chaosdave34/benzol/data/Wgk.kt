package io.github.chaosdave34.benzol.data

enum class Wgk(val label: String) {
    NONE("-"),
    WGK_1("WGK 1"),
    WGK_2("WGK 2"),
    WGK_3("WGK 3");

    companion object {
        fun fromLabel(label: String): Wgk {
            return entries.find { it.label == label } ?: NONE
        }
    }
}