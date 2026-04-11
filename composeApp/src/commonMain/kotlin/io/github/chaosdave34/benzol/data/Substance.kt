package io.github.chaosdave34.benzol.data

import benzol.composeapp.generated.resources.Res
import benzol.composeapp.generated.resources.milli_gram_per_cubic_metre
import benzol.composeapp.generated.resources.milli_litre_per_cubic_metre
import io.github.chaosdave34.benzol.search.Source
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Serializable
data class Substance(
    val nameModifiable: Modifiable<String> = Modifiable(""),
    val casNumberModifiable: Modifiable<String> = Modifiable(""),
    val molecularFormulaModifiable: Modifiable<String> = Modifiable(""),
    val wgkModifiable: Modifiable<Wgk> = Modifiable(Wgk.NONE),
    val signalWordModifiable: Modifiable<SignalWord> = Modifiable(SignalWord.NONE),
    val molarMassModifiable: Modifiable<String> = Modifiable(""),
    val lethalDoseModifiable: Modifiable<String> = Modifiable(""),
    val makModifiable: Modifiable<String> = Modifiable(""),
    val makUnitModifiable: Modifiable<MakUnit> = Modifiable(MakUnit.MILLI_GRAM_PER_CUBIC_METRE), // Todo different value so no new export file format needed
    val meltingPointModifiable: Modifiable<String> = Modifiable(""),
    val boilingPointModifiable: Modifiable<String> = Modifiable(""),
    val decompositionTemperatureModifiable: Modifiable<String> = Modifiable(""),

    var quantity: Quantity = Quantity(),
    val hazardStatementsModifiable: Modifiable<List<Pair<String, String>>> = Modifiable(emptyList()),
    val precautionaryStatementsModifiable: Modifiable<List<Pair<String, String>>> = Modifiable(emptyList()),
    val ghsPictogramsModifiable: Modifiable<Set<GHSPictogram>> = Modifiable(emptySet()),

    var source: Pair<Source, String> = Pair(Source.Custom, "")
) {
    constructor(
        name: String,
        casNumber: String,
        molecularFormula: String,
        wgk: Wgk,
        signalWord: SignalWord,
        molarMass: String,
        lethalDose: String,
        mak: String,
        makUnit: MakUnit,
        meltingPoint: String,
        boilingPoint: String,
        decompositionTemperature: String,
        hazardStatements: List<Pair<String, String>>,
        precautionaryStatements: List<Pair<String, String>>,
        ghsPictograms: Set<GHSPictogram>,
        source: Pair<Source, String>
    ) : this(
        nameModifiable = Modifiable(name),
        casNumberModifiable = Modifiable(casNumber),
        molecularFormulaModifiable = Modifiable(molecularFormula),
        wgkModifiable = Modifiable(wgk),
        signalWordModifiable = Modifiable(signalWord),
        molarMassModifiable = Modifiable(molarMass),
        lethalDoseModifiable = Modifiable(lethalDose),
        makModifiable = Modifiable(mak),
        makUnitModifiable = Modifiable(makUnit),
        meltingPointModifiable = Modifiable(meltingPoint),
        boilingPointModifiable = Modifiable(boilingPoint),
        decompositionTemperatureModifiable = Modifiable(decompositionTemperature),
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
    val makUnit by ModifiableDelegate(makUnitModifiable)
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

    enum class MakUnit(override val label: StringResource) : Labeled {
        MILLI_GRAM_PER_CUBIC_METRE(Res.string.milli_gram_per_cubic_metre),
        MILLI_LITRE_PER_CUBIC_METRE(Res.string.milli_litre_per_cubic_metre)
    }

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