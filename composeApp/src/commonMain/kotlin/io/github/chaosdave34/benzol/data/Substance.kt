package io.github.chaosdave34.benzol.data

import io.github.chaosdave34.benzol.search.Source
import kotlinx.serialization.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Serializable
data class Substance(
    val nameModifiable: Modifiable<String>,
    val casNumberModifiable: Modifiable<String>,
    val molecularFormulaModifiable: Modifiable<String>,
    val wgkModifiable: Modifiable<Wgk>,
    val signalWordModifiable: Modifiable<SignalWord>,
    val molarMassModifiable: Modifiable<String>,
    val lethalDoseModifiable: Modifiable<String>,
    val makModifiable: Modifiable<String>,
    val meltingPointModifiable: Modifiable<String>,
    val boilingPointModifiable: Modifiable<String>,

    var quantity: Quantity = Quantity(),
    val hazardStatementsModifiable: Modifiable<List<Pair<String, String>>>,
    val precautionaryStatementsModifiable: Modifiable<List<Pair<String, String>>>,
    val ghsPictogramsModifiable: Modifiable<Set<GHSPictogram>>,

    var source: Pair<Source, String>
) {
    constructor(
        name: String = "",
        casNumber: String = "",
        molecularFormula: String = "",
        wgk: Wgk = Wgk.NONE,
        signalWord: SignalWord = SignalWord.NONE,
        molarMass: String = "",
        lethalDose: String = "",
        mak: String = "",
        meltingPoint: String = "",
        boilingPoint: String = "",
        hazardStatements: List<Pair<String, String>> = emptyList(),
        precautionaryStatements: List<Pair<String, String>> = emptyList(),
        ghsPictograms: Set<GHSPictogram> = emptySet(),
        source: Pair<Source, String> = Pair(Source.Custom, "")
    ) : this(
        nameModifiable = Modifiable(name),
        casNumberModifiable = Modifiable(casNumber),
        molecularFormulaModifiable = Modifiable(molecularFormula),
        wgkModifiable = Modifiable(wgk),
        signalWordModifiable = Modifiable(signalWord),
        molarMassModifiable = Modifiable(molarMass),
        lethalDoseModifiable = Modifiable(lethalDose),
        makModifiable = Modifiable(mak),
        meltingPointModifiable = Modifiable(meltingPoint),
        boilingPointModifiable = Modifiable(boilingPoint),
        quantity = Quantity(""),
        hazardStatementsModifiable = Modifiable(hazardStatements),
        precautionaryStatementsModifiable = Modifiable(precautionaryStatements),
        ghsPictogramsModifiable = Modifiable(ghsPictograms),
        source = source
    )

    companion object {
        fun formatStatements(list: List<Substance>, transform: (Substance) -> List<Pair<String, String>>): List<Pair<String, String>> {
            return list.flatMap(transform).distinctBy { it.first }.sortedBy { it.first }
        }

        fun sources(list: List<Substance>): List<Source> {
            return list.map { it.source.first }.filter { it != Source.Custom }.distinct()
        }
    }

    val name by ModifiableDelegate(nameModifiable)
    val casNumber by ModifiableDelegate(casNumberModifiable)
    val molecularFormula by ModifiableDelegate(molecularFormulaModifiable)
    val wgk by ModifiableDelegate(wgkModifiable)
    val signalWord by ModifiableDelegate(signalWordModifiable)

    val molarMass by ModifiableDelegate(molarMassModifiable)
    val lethalDose by ModifiableDelegate(lethalDoseModifiable)
    val mak by ModifiableDelegate(makModifiable)
    val meltingPoint by ModifiableDelegate(meltingPointModifiable)
    val boilingPoint by ModifiableDelegate(boilingPointModifiable)
    val decompositionTemperature by ModifiableDelegate(decompositionTemperatureModifiable)

    val hazardStatements by ModifiableDelegate(hazardStatementsModifiable)
    val precautionaryStatements by ModifiableDelegate(precautionaryStatementsModifiable)
    val ghsPictograms by ModifiableDelegate(ghsPictogramsModifiable)

    private class ModifiableDelegate<T>(val modifiable: Modifiable<T>) : ReadOnlyProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = modifiable.current
    }

    @Serializable
    data class Quantity(val value: String = "", val unit: String = "g")

    fun copyOriginal() = copy(
        nameModifiable = nameModifiable.copy(modified = null),
        casNumberModifiable = casNumberModifiable.copy(modified = null),
        molecularFormulaModifiable = molecularFormulaModifiable.copy(modified = null),
        wgkModifiable = wgkModifiable.copy(modified = null),
        signalWordModifiable = signalWordModifiable.copy(modified = null),
        molarMassModifiable = molarMassModifiable.copy(modified = null),
        lethalDoseModifiable = lethalDoseModifiable.copy(modified = null),
        makModifiable = makModifiable.copy(modified = null),
        meltingPointModifiable = meltingPointModifiable.copy(modified = null),
        boilingPointModifiable = boilingPointModifiable.copy(modified = null),
        quantity = Quantity(),
        hazardStatementsModifiable = hazardStatementsModifiable.copy(modified = null),
        precautionaryStatementsModifiable = precautionaryStatementsModifiable.copy(modified = null),
        ghsPictogramsModifiable = ghsPictogramsModifiable.copy(modified = null),
    )
}