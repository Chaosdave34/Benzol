package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle

@Composable
fun FormattedMolecularFormula(
    modifier: Modifier = Modifier,
    formula: String,
    textAlign: TextAlign? = null
) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            var sub = false
            val splits = formula.split("[<>]".toRegex())
            splits.forEach {
                if (sub) {
                    withStyle(
                        style = SpanStyle(
                            baselineShift = BaselineShift.Subscript,
                            fontSize = LocalTextStyle.current.fontSize * 0.7
                        )
                    ) {
                        append(it)
                    }
                } else {
                    append(it)
                }
                sub = !sub
            }
        },
        textAlign = textAlign
    )
}