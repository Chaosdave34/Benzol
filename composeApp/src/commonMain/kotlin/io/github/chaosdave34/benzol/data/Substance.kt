package io.github.chaosdave34.benzol.data

import io.github.chaosdave34.benzol.search.Source
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

data class Substance(
    val nameModifiable: Modifiable<String>,
    val casNumberModifiable: Modifiable<String>,
    val molecularFormulaModifiable: Modifiable<String>,
    val formattedMolecularFormulaModifiable: Modifiable<String>,
    val wgkModifiable: Modifiable<String>,
    val signalWordModifiable: Modifiable<String>,
    val molarMassModifiable: Modifiable<String>,
    val lethalDoseModifiable: Modifiable<String>,
    val makModifiable: Modifiable<String>,
    val meltingPointModifiable: Modifiable<String>,
    val boilingPointModifiable: Modifiable<String>,

    var quantity: Quantity = Quantity(),
    val hPhrasesModifiable: Modifiable<List<Pair<String, String>>>,
    val pPhrasesModifiable: Modifiable<List<Pair<String, String>>>,
    val ghsPictogramsModifiable: Modifiable<List<GHSPictogram>>,

    var source: Pair<Source, String>
) {
    constructor(
        name: String = "",
        casNumber: String = "",
        molecularFormula: String = "",
        formattedMolecularFormula: String = "",
        wgk: String = "",
        signalWord: String = "",
        molarMass: String = "",
        lethalDose: String = "",
        mak: String = "",
        meltingPoint: String = "",
        boilingPoint: String = "",
        hPhrases: List<Pair<String, String>> = emptyList(),
        pPhrases: List<Pair<String, String>> = emptyList(),
        ghsPictograms: List<GHSPictogram> = emptyList(),
        source: Pair<Source, String> = Pair(Source.Custom, "")
    ) : this(
        nameModifiable = Modifiable(name),
        casNumberModifiable = Modifiable(casNumber),
        molecularFormulaModifiable = Modifiable(molecularFormula),
        formattedMolecularFormulaModifiable = Modifiable(formattedMolecularFormula),
        wgkModifiable = Modifiable(wgk),
        signalWordModifiable = Modifiable(signalWord),
        molarMassModifiable = Modifiable(molarMass),
        lethalDoseModifiable = Modifiable(lethalDose),
        makModifiable = Modifiable(mak),
        meltingPointModifiable = Modifiable(meltingPoint),
        boilingPointModifiable = Modifiable(boilingPoint),
        quantity = Quantity(""),
        hPhrasesModifiable = Modifiable(hPhrases),
        pPhrasesModifiable = Modifiable(pPhrases),
        ghsPictogramsModifiable = Modifiable(ghsPictograms),
        source = source
    )

    companion object {
        fun formatPhrases(list: List<Substance>, transform: (Substance) -> List<Pair<String, String>>): List<Pair<String, String>> {
            return list.map(transform).flatten().distinctBy { it.first }.sortedBy { it.first }
        }

        fun sources(list: List<Substance>): List<Source> {
            return list.map { it.source.first }.filter { it != Source.Custom }.distinct()
        }
    }

    var name by ModifiableDelegate(nameModifiable)
    var casNumber by ModifiableDelegate(casNumberModifiable)
    var molecularFormula by ModifiableDelegate(molecularFormulaModifiable)
    var formattedMolecularFormula by ModifiableDelegate(formattedMolecularFormulaModifiable)
    var wgk by ModifiableDelegate(wgkModifiable)
    var signalWord by ModifiableDelegate(signalWordModifiable)

    var molarMass by ModifiableDelegate(molarMassModifiable)
    var lethalDose by ModifiableDelegate(lethalDoseModifiable)
    var mak by ModifiableDelegate(makModifiable)
    var meltingPoint by ModifiableDelegate(meltingPointModifiable)
    var boilingPoint by ModifiableDelegate(boilingPointModifiable)

    var hPhrases by ModifiableDelegate(hPhrasesModifiable)
    var pPhrases by ModifiableDelegate(pPhrasesModifiable)
    var ghsPictograms by ModifiableDelegate(ghsPictogramsModifiable)

    private class ModifiableDelegate<T>(val modifiable: Modifiable<T>) : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = modifiable.current

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            modifiable.modified = value
        }
    }

    data class Quantity(val value: String = "", val unit: String = "g")

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
            nameModifiable = nameModifiable.copy(modified = name),
            casNumberModifiable = casNumberModifiable.copy(modified = casNumber),
            molecularFormulaModifiable = molecularFormulaModifiable.copy(modified = molecularFormula),
            formattedMolecularFormulaModifiable = formattedMolecularFormulaModifiable.copy(modified = formattedMolecularFormula),
            wgkModifiable = wgkModifiable.copy(modified = wgk),
            signalWordModifiable = signalWordModifiable.copy(modified = signalWord),
            molarMassModifiable = molarMassModifiable.copy(modified = molarMass),
            lethalDoseModifiable = lethalDoseModifiable.copy(modified = lethalDose),
            makModifiable = makModifiable.copy(modified = mak),
            meltingPointModifiable = meltingPointModifiable.copy(modified = meltingPoint),
            boilingPointModifiable = boilingPointModifiable.copy(modified = boilingPoint),
            quantity = quantity.copy(value = quantity.value, unit = quantity.unit),
            hPhrasesModifiable = hPhrasesModifiable.copy(modified = hPhrases),
            pPhrasesModifiable = pPhrasesModifiable.copy(modified = pPhrases),
            ghsPictogramsModifiable = ghsPictogramsModifiable.copy(modified = ghsPictograms)
        )
    }
}