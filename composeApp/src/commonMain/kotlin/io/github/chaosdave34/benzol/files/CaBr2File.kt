package io.github.chaosdave34.benzol.files

import io.github.chaosdave34.benzol.data.*
import io.github.chaosdave34.benzol.files.CaBr2File.CaBr2Data.ValuePair.Companion.toListModifiable
import io.github.chaosdave34.benzol.files.CaBr2File.CaBr2Data.ValuePair.Companion.toModifiable
import io.github.chaosdave34.benzol.files.CaBr2File.CaBr2Data.ValuePair.Companion.toStringModifiable
import io.github.chaosdave34.benzol.files.export.Savable
import kotlinx.serialization.Serializable

object CaBr2File {

    @Serializable
    data class CaBr2Data(
        val header: Header,
        val substanceData: List<SubstanceData>,
        val humanAndEnvironmentDanger: List<String>,
        val rulesOfConduct: List<String>,
        val inCaseOfDanger: List<String>,
        val disposal: List<String>
    ) : Savable {
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
            fun import(): Substance {
                return Substance(
                    name.toStringModifiable(),
                    cas.toStringModifiable(),
                    molecularFormula.toStringModifiable(),
                    molecularFormula.toStringModifiable(),
                    waterHazardClass.toModifiable(default = Wgk.NONE) { Wgk.fromLabel(this) },
                    signalWord.toModifiable(default = SignalWord.NONE) { SignalWord.fromLabel(this) },
                    molarMass.toStringModifiable(),
                    lethalDose.toStringModifiable { replace(" mg/kg", "") },
                    mak.toStringModifiable { replace(" mg/m³", "") },
                    meltingPoint.toStringModifiable { replace(" °C", "") },
                    boilingPoint.toStringModifiable { replace(" °C", "") },
                    amount?.toQuantity() ?: Substance.Quantity(),
                    hPhrases.toListModifiable { Pair(it.getOrElse(0) { "" }.trim(), it.getOrElse(1) { "" }.trim()) },
                    pPhrases.toListModifiable { Pair(it.getOrElse(0) { "" }.trim(), it.getOrElse(1) { "" }.trim()) },
                    symbols.toListModifiable { GHSPictogram.fromId(it) },
                    Pair(source.getSource(), source.url.trim())
                )
            }
        }

        @Serializable
        class Source(
            val provider: String,
            val url: String,
            @Suppress("unused") val lastUpdated: String
        ) {
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
                fun <T> ValuePair<String>.toModifiable(default: T, transformer: String.() -> T): Modifiable<T> {
                    return Modifiable(originalData?.run(transformer) ?: default, modifiedData?.run(transformer))
                }

                fun ValuePair<String>.toStringModifiable(transformer: String.() -> String = { this }): Modifiable<String> {
                    return Modifiable(originalData?.run(transformer)?.trim() ?: "", modifiedData?.run(transformer)?.trim())
                }

                fun <I, O> ValuePair<List<I>>.toListModifiable(map: (I) -> O?): Modifiable<List<O>> {
                    return Modifiable(originalData?.mapNotNull(map) ?: emptyList(), modifiedData?.mapNotNull(map))
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
                val unit = when (unit.type) {
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