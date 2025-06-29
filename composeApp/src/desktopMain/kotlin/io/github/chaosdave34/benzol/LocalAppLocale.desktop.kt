package io.github.chaosdave34.benzol

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.*

actual object LocalAppLocale {
    private var default: Locale? = null
    private val LocalAppLocale = staticCompositionLocalOf { Locale.getDefault().toString() }

    actual val current: String
        @Composable get() = LocalAppLocale.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        if (default == null) {
            default = Locale.getDefault()
        }
        val new = when (value) {
            null -> default!!
            else -> Locale.of(value)
        }
        Locale.setDefault(new)
        return LocalAppLocale.provides(new.toString())
    }
}