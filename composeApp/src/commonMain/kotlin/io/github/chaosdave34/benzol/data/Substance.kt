package io.github.chaosdave34.benzol.data

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import io.github.chaosdave34.benzol.search.Source
import kotlinx.serialization.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

data class Substance(
    val namePair: StringPair = StringPair(),
    val casNumberPair: StringPair = StringPair(),
    val molecularFormulaPair: StringPair = StringPair(),
    val formattedMolecularFormulaPair: StringPair = StringPair(),
    val wgkPair: StringPair = StringPair(),
    val signalWordPair: StringPair = StringPair(),

    val molarMassPair: StringPair = StringPair(),
    val lethalDosePair: StringPair = StringPair(),
    val makPair: StringPair = StringPair(),
    val meltingPointPair: StringPair = StringPair(),
    val boilingPointPair: StringPair = StringPair(),
    var quantity: Quantity = Quantity(),

    var hPhrasesPair: ListPair<Pair<String, String>> = ListPair(),
    var pPhrasesPair: ListPair<Pair<String, String>> = ListPair(),
    var ghsPictogramsPair: ListPair<GHSPictogram> = ListPair(),

    var source: Pair<Source, String> = Pair(Source.Custom, "")
) {

    companion object {
        fun fromSource(
            name: String,
            casNumber: String,
            molecularFormula: String,
            formattedMolecularFormula: String,
            wgk: String,
            signalWord: String,

            molarMass: String,
            lethalDose: String,
            mak: String,
            meltingPoint: String,
            boilingPoint: String,
            hPhrases: List<Pair<String, String>>,
            pPhrases: List<Pair<String, String>>,
            ghsPictograms: List<GHSPictogram>,
            source: Pair<Source, String>
        ) = Substance(
            StringPair(original = name),
            StringPair(original = casNumber),
            StringPair(original = molecularFormula),
            StringPair(original = formattedMolecularFormula),
            StringPair(original = wgk),
            StringPair(original = signalWord),
            StringPair(original = molarMass),
            StringPair(original = lethalDose),
            StringPair(original = mak),
            StringPair(original = meltingPoint),
            StringPair(original = boilingPoint),
            Quantity(),
            ListPair(original = hPhrases),
            ListPair(original = pPhrases),
            ListPair(original = ghsPictograms),
            source
        )

        fun formatPhrases(list: List<Substance>, transform: (Substance) -> List<Pair<String, String>>): List<Pair<String, String>> {
            return list.map(transform).flatten().distinctBy { it.first }.sortedBy { it.first }
        }

        fun sources(list: List<Substance>): List<Source> {
            return list.map { it.source.first }.filter { it != Source.Custom }.distinct()
        }
    }

    var name by StringPairDelegate(namePair)
    var casNumber by StringPairDelegate(casNumberPair)
    var molecularFormula by StringPairDelegate(molecularFormulaPair)
    var formattedMolecularFormula by StringPairDelegate(formattedMolecularFormulaPair)
    var wgk by StringPairDelegate(wgkPair)
    var signalWord by StringPairDelegate(signalWordPair)

    var molarMass by StringPairDelegate(molarMassPair)
    var lethalDose by StringPairDelegate(lethalDosePair)
    var mak by StringPairDelegate(makPair)
    var meltingPoint by StringPairDelegate(meltingPointPair)
    var boilingPoint by StringPairDelegate(boilingPointPair)

    var hPhrases by ListPairDelegate(hPhrasesPair)
    var pPhrases by ListPairDelegate(pPhrasesPair)
    var ghsPictograms by ListPairDelegate(ghsPictogramsPair)

    @Serializable
    data class StringPair(val original: String = "", var modified: String? = null) {
        fun get() = modified ?: original

    }

    data class ListPair<T>(val original: List<T> = emptyList(), var modified: List<T>? = null) {
        fun get() = modified ?: original

    }

    data class Quantity(val value: String = "", val unit: String = "g")

    private class StringPairDelegate(val stringPair: StringPair) : ReadWriteProperty<Substance, String> {
        override fun getValue(thisRef: Substance, property: KProperty<*>) = stringPair.get()


        override fun setValue(thisRef: Substance, property: KProperty<*>, value: String) {
            stringPair.modified = value
        }
    }

    private class ListPairDelegate<T>(val listPair: ListPair<T>) : ReadWriteProperty<Substance, List<T>> {
        override fun getValue(thisRef: Substance, property: KProperty<*>) = listPair.get()

        override fun setValue(thisRef: Substance, property: KProperty<*>, value: List<T>) {
            listPair.modified = value
        }
    }

    fun copyAsModified(
        name: String,
        casNumber: String,
        molecularFormula: String,
        formattedMolecularFormula: String,
        wgk: String,
        signalWord: String,
        molarMass: String,
        lethalDose: String,
        mak: String,
        meltingPoint: String,
        boilingPoint: String,
        quantity: Quantity,
        hPhrases: List<Pair<String, String>>,
        pPhrases: List<Pair<String, String>>,
        ghsPictograms: List<GHSPictogram>
    ): Substance {
        return copy(
            namePair = namePair.copy(modified = name),
            casNumberPair = casNumberPair.copy(modified = casNumber),
            molecularFormulaPair = molecularFormulaPair.copy(modified = molecularFormula),
            formattedMolecularFormulaPair = formattedMolecularFormulaPair.copy(modified = formattedMolecularFormula),
            wgkPair = wgkPair.copy(modified = wgk),
            signalWordPair = signalWordPair.copy(modified = signalWord),
            molarMassPair = molarMassPair.copy(modified = molarMass),
            lethalDosePair = lethalDosePair.copy(modified = lethalDose),
            makPair = makPair.copy(modified = mak),
            meltingPointPair = meltingPointPair.copy(modified = meltingPoint),
            boilingPointPair = boilingPointPair.copy(modified = boilingPoint),
            quantity = quantity.copy(value = quantity.value, unit = quantity.unit),
            hPhrasesPair = hPhrasesPair.copy(modified = hPhrases),
            pPhrasesPair = pPhrasesPair.copy(modified = pPhrases),
            ghsPictogramsPair = ghsPictogramsPair.copy(modified = ghsPictograms)
        )

    }

    @Composable
    fun FormattedMolecularFormula(modifier: Modifier = Modifier.Companion, formula: String = formattedMolecularFormula, align: TextAlign? = null) {
        var sub = false
        val splits = formula.split("[<>]".toRegex())
        Text(
            modifier = modifier,
            text = buildAnnotatedString {
                splits.forEach {
                    if (sub) {
                        withStyle(
                            style = SpanStyle(
                                baselineShift = BaselineShift.Subscript,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize
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
            textAlign = align
        )
    }
}