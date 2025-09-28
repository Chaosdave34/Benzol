package io.github.chaosdave34.benzol.files

import io.github.chaosdave34.benzol.data.GHSPictogram
import io.github.chaosdave34.benzol.data.Substance
import io.github.chaosdave34.benzol.files.CaBr2File.CaBr2Data.Source.Companion.toProvider
import io.github.chaosdave34.benzol.files.CaBr2File.CaBr2Data.ValuePair.Companion.toListPair
import io.github.chaosdave34.benzol.files.CaBr2File.CaBr2Data.ValuePair.Companion.toStringPair
import io.github.chaosdave34.benzol.files.CaBr2File.CaBr2Data.ValuePair.Companion.toValuePair
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object CaBr2File {

    fun fromJson(value: String): CaBr2Data? {
        return try {
            Json.decodeFromString<CaBr2Data>(value)
        } catch (_: Exception) {
            null
        }
    }

    fun toJson(value: CaBr2Data): String {
        return Json.encodeToString(value)
    }

    @Serializable
    data class CaBr2Data(
        val header: Header,
        val substanceData: List<SubstanceData>,
        val humanAndEnvironmentDanger: List<String>,
        val rulesOfConduct: List<String>,
        val inCaseOfDanger: List<String>,
        val disposal: List<String>
    ) {
        @Serializable
        data class Header(
            val documentTitle: String,
            val organisation: String,
            val labCourse: String,
            val name: String,
            val place: String,
            val assistant: String,
            val preparation: String,
        )

        @Serializable
        data class SubstanceData(
            val name: ValuePair<String>,
            val alternativeNames: List<String>,
            val cas: ValuePair<String>,
            val molecularFormula: ValuePair<String>,
            val formattedMolecularFormula: Substance.StringPair? = null, // Normally not in cb2 files
            val molarMass: ValuePair<String>,
            val meltingPoint: ValuePair<String>,
            val boilingPoint: ValuePair<String>,
            val waterHazardClass: ValuePair<String>,
            val hPhrases: ValuePair<List<List<String>>>,
            val pPhrases: ValuePair<List<List<String>>>,
            val signalWord: ValuePair<String>,
            val symbols: ValuePair<List<String>>,
            val lethalDose: ValuePair<String>,
            val mak: ValuePair<String>,
            val amount: Amount? = null,
            val source: Source,
            val checked: Boolean
        ) {
            companion object {
                fun export(substance: Substance): SubstanceData {
                    return SubstanceData(
                        substance.namePair.toValuePair(),
                        emptyList(),
                        substance.casNumberPair.toValuePair(),
                        substance.molecularFormulaPair.toValuePair(),
                        substance.formattedMolecularFormulaPair,
                        substance.molarMassPair.toValuePair(),
                        substance.meltingPointPair.toValuePair { if (this.isNotBlank()) "$this °C" else this },
                        substance.boilingPointPair.toValuePair { if (this.isNotBlank()) "$this °C" else this },
                        substance.wgkPair.toValuePair(),
                        substance.hPhrasesPair.toValuePair { listOf(it.first, it.second) },
                        substance.pPhrasesPair.toValuePair { listOf(it.first, it.second) },
                        substance.signalWordPair.toValuePair(),
                        substance.ghsPictogramsPair.toValuePair { ghsPictogramToSymbols(it) },
                        substance.lethalDosePair.toValuePair { if (this.isNotBlank()) "$this mg/kg" else this },
                        substance.makPair.toValuePair { if (this.isNotBlank()) "$this mg/m³" else this },
                        if (substance.quantity.value.isBlank()) null else Amount(substance.quantity.value, Amount.Unit("CUSTOM", substance.quantity.unit)),
                        Source(substance.source.first.toProvider(), substance.source.second, ""),
                        false
                    )
                }

                fun ghsPictogramToSymbols(ghsPictogram: GHSPictogram): String {
                    return when (ghsPictogram) {
                        GHSPictogram.Explosion -> "ghs01"
                        GHSPictogram.Flame -> "ghs02"
                        GHSPictogram.FlameOverCircle -> "ghs03"
                        GHSPictogram.GasBottle -> "ghs04"
                        GHSPictogram.Acid -> "ghs05"
                        GHSPictogram.Skull -> "ghs06"
                        GHSPictogram.Exclamation -> "ghs07"
                        GHSPictogram.Silhouette -> "ghs08"
                        GHSPictogram.Nature -> "ghs09"
                    }
                }
            }

            fun import(): Substance {
                return Substance(
                    name.toStringPair(),
                    cas.toStringPair(),
                    molecularFormula.toStringPair(),
                    formattedMolecularFormula ?: Substance.StringPair(),
                    waterHazardClass.toStringPair(),
                    signalWord.toStringPair(),
                    molarMass.toStringPair(),
                    lethalDose.toStringPair { replace(" mg/kg", "") },
                    mak.toStringPair { replace(" mg/m³", "") },
                    meltingPoint.toStringPair { replace(" °C", "") },
                    boilingPoint.toStringPair { replace(" °C", "") },
                    amount?.toQuantity()?: Substance.Quantity(),
                    hPhrases.toListPair { Pair(it.getOrElse(0) { "" }.trim(), it.getOrElse(1) { "" }.trim()) },
                    pPhrases.toListPair { Pair(it.getOrElse(0) { "" }.trim(), it.getOrElse(1) { "" }.trim()) },
                    symbols.toListPair { GHSPictogram.fromId(it) },
                    Pair(source.getSource(), source.url.trim())
                )
            }
        }

        @Serializable
        class Source(
            val provider: String,
            val url: String,
            val lastUpdated: String
        ) {
            companion object {
                fun io.github.chaosdave34.benzol.search.Source.toProvider(): String {
                    return when (this) {
                        io.github.chaosdave34.benzol.search.Source.Gestis -> "gestis"
                        io.github.chaosdave34.benzol.search.Source.Custom -> "custom"
                    }
                }
            }


            fun getSource(): io.github.chaosdave34.benzol.search.Source {
                return when (provider) {
                    "gestis" -> io.github.chaosdave34.benzol.search.Source.Gestis
                    else -> io.github.chaosdave34.benzol.search.Source.Custom
                }
            }

        }

        @Serializable
        class ValuePair<T>(
            val originalData: T? = null,
            val modifiedData: T? = null
        ) {
            companion object {
                fun ValuePair<String>.toStringPair(transformer: String.() -> String = { this }): Substance.StringPair {
                    return Substance.StringPair(originalData?.run(transformer)?.trim() ?: "", modifiedData?.run(transformer)?.trim())
                }

                fun <I, O> ValuePair<List<I>>.toListPair(map: (I) -> O?): Substance.ListPair<O> {
                    return Substance.ListPair(originalData?.mapNotNull(map) ?: emptyList(), modifiedData?.mapNotNull(map))
                }

                fun Substance.StringPair.toValuePair(transformer: String.() -> String = { this }): ValuePair<String> {
                    return ValuePair(this.original.run(transformer).trim(), this.modified?.run(transformer)?.trim())
                }

                fun <I, O> Substance.ListPair<I>.toValuePair(map: (I) -> O): ValuePair<List<O>> {
                    return ValuePair(this.original.map(map), this.modified?.map(map))
                }
            }

            fun get(): T? {
                return modifiedData ?: originalData
            }

            fun get(default: T): T {
                return get() ?: default
            }
        }

        @Serializable
        data class Amount(
            val value: String,
            val unit: Unit
        ) {
            @Serializable
            data class Unit(
                val type: String,
                val name: String? = null
            )

            fun toQuantity(): Substance.Quantity {
                val unit = when(unit.type) {
                    "CUSTOM" -> unit.name ?: "g"
                    "LITER" -> "l"
                    "MILLILITER" -> "ml"
                    "MICROLITER" -> "µl"
                    "GRAM" -> "g"
                    "MILLIGRAM" -> "mg"
                    "MICROGRAM" -> "µg"
                    "MOL" -> "mol"
                    "MILLIMOL" -> "mmol"
                    "PIECES" -> "st"
                    "SOLUTION_RELATIVE" -> "$ (v/v)"
                    "SOLUTION_MOL" -> "mol/l"
                    "SOLUTION_MILLIMOL" -> "mmol/l"
                    "SOLUTION_MICROMOL" -> "µmol/l"
                    "SOLUTION_GRAM" -> "g/mol"
                    "SOLUTION_MILLIGRAM" -> "mg/l"
                    "GRAM_PER_MOL" -> "g/mol"
                    "MILLIGRAM_PER_KILOGRAM" -> "mg/kg"
                    "MILLIGRAM_PER_LITER" -> "mg/l"
                    "PART_PER_MILLION" -> "ppm"
                    "CELSIUS" -> "°C"
                    "FAHRENHEIT" -> "F"
                    else -> "g"
                }

                return Substance.Quantity(value, unit)
            }
        }

    }
}