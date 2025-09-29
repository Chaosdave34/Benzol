package io.github.chaosdave34.benzol.files

import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.data.Substance
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.getString
import kotlin.text.Typography.nbsp

suspend fun createHtml(
    data: SafetySheetInputState,
    resourceEnvironment: ResourceEnvironment
): String {
    val assistantTitle = getString(resourceEnvironment, Res.string.assistant)
    val preparationTitle = getString(resourceEnvironment, Res.string.preparation)
    val nameTitle = getString(resourceEnvironment, Res.string.name_with_plural)
    val placeTitle = getString(resourceEnvironment, Res.string.place)

    val usedSubstancesTitle = getString(resourceEnvironment, Res.string.used_substances)
    val molarMassTitle = getString(resourceEnvironment, Res.string.molar_mass_with_unit)
    val temperaturesTitle = getString(resourceEnvironment, Res.string.temperatures)
    val ghsSymbolsTitle = getString(resourceEnvironment, Res.string.ghs_pictograms)
    val hAndPPhrasesNumberTitle = getString(resourceEnvironment, Res.string.h_and_p_phrases_number)
    val makLd50WgkTitle = getString(resourceEnvironment, Res.string.mak_ld50_wgk)

    val quantityTitle = getString(resourceEnvironment, Res.string.quantity_required)
    val makUnit = getString(resourceEnvironment, Res.string.mak_unit)
    val lethalDoseUnit = getString(resourceEnvironment, Res.string.lethal_dose_unit)
    val celsiusUnit = getString(resourceEnvironment, Res.string.celsius_unit)

    val hAndPPhrasesTitle = getString(resourceEnvironment, Res.string.title_h_and_p_phrases)
    val sourcesTitle = getString(resourceEnvironment, Res.string.sources) + ":"
    val humanAndEnvironmentDangerTitle = getString(resourceEnvironment, Res.string.human_and_environment_danger) + ":"
    val rulesOfConductTitle = getString(resourceEnvironment, Res.string.rules_of_conduct) + ":"
    val inCaseOfDangerTitle = getString(resourceEnvironment, Res.string.in_case_of_danger) + ":"
    val disposalTitle = getString(resourceEnvironment, Res.string.disposal) + ":"

    val signature = getString(resourceEnvironment, Res.string.signature)
    val locationAndDate = getString(resourceEnvironment, Res.string.location_and_date)
    val signature1 = getString(resourceEnvironment, Res.string.signature_1)
    val signature2 = getString(resourceEnvironment, Res.string.signature_2)

    val css = Res.readBytes("files/export.css").decodeToString()

    val sources = Substance.sources(data.substances).map { getString(it.label) }.joinToString(", ")

    return buildString {
        appendLine("<!DOCTYPE html>")
        appendHTML().html {
            head {
                meta(charset = "UTF-8")
                meta(name = "viewport", content = "width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0")
                style {
                    unsafe {
                        raw(css)
                    }
                }
            }
            body {
                // page 1
                table {
                    // header
                    header(data.documentTitle.trim())
                    header(data.organisation.trim())
                    header(data.course.trim())
                    tr {
                        textBlock(13, nameTitle, data.name.trim())
                        textBlock(8, placeTitle, data.place.trim())
                        textBlock(13, assistantTitle, data.assistant.trim())
                    }
                    tr {
                        td {
                            colSpan = "34"
                            +preparationTitle
                            br
                            b {
                                +data.preparation.trim()
                            }
                        }
                    }

                    // ingredients
                    tr {
                        ingredientTitle(6, usedSubstancesTitle)
                        ingredientTitle(4, molarMassTitle)
                        ingredientTitle(4, temperaturesTitle)
                        ingredientTitle(6, ghsSymbolsTitle)
                        ingredientTitle(6, hAndPPhrasesNumberTitle)
                        ingredientTitle(4, makLd50WgkTitle)
                        ingredientTitle(4, quantityTitle)
                    }
                    data.substances.forEach { substance ->
                        tr {
                            td("min-width-5cm center") {
                                colSpan = "6"
                                +substance.name
                                br
                                if (substance.formattedMolecularFormula.isNotBlank()) {
                                    val formula = substance.formattedMolecularFormula

                                    var sub = formula.startsWith("<")
                                    val splits = formula.split("[<>]".toRegex())
                                    splits.forEach {
                                        if (sub) {
                                            sub {
                                                +it
                                            }
                                        } else {
                                            +it
                                        }
                                        sub = !sub
                                    }
                                } else {
                                    +substance.molecularFormula
                                }
                            }
                            td("min-width-2cm value-with-unit center") {
                                colSpan = "4"
                                +valueOrDash(substance.molarMass)
                            }
                            td("min-width-2cm value-with-unit center") {
                                colSpan = "4"
                                +valueOrDash(substance.boilingPoint, celsiusUnit)
                                br
                                +valueOrDash(substance.meltingPoint, celsiusUnit)
                            }
                            td("min-width-ghs-symbols center") {
                                colSpan = "6"
                                substance.ghsPictograms.forEach {
                                    img(classes = "ghs") {
                                        src = "data:image/png;base64,${it.base64String}"
                                        alt = it.alt
                                    }
                                }
                                if (substance.signalWord.isNotBlank()) {
                                    p("signalword") {
                                        +substance.signalWord
                                    }
                                }
                            }
                            td("phrase-numbers center") {
                                colSpan = "6"
                                +substance.hPhrases.joinToString("-") { it.first }
                                br
                                br
                                +substance.pPhrases.joinToString("-") { it.first }
                            }
                            td("min-width-2cm value-with-unit center") {
                                colSpan = "4"
                                +valueOrDash(substance.mak, makUnit)
                                br
                                +valueOrDash(substance.lethalDose, lethalDoseUnit)
                                br
                                +valueOrDash(substance.wgk)
                            }
                            td("center") {
                                colSpan = "4"
                                if (substance.quantity.value.isNotBlank()) {
                                    +substance.quantity.value
                                    nbsp()
                                    +substance.quantity.unit
                                }
                            }
                        }
                    }

                    // h and p
                    tr("no-break-after") {
                        td("center") {
                            colSpan = "34"
                            +hAndPPhrasesTitle
                        }
                    }
                    tr("no-break") {
                        phrasesList(data.substances) { it.hPhrases }
                        phrasesList(data.substances) { it.pPhrases }
                    }
                    tr {
                        td {
                            colSpan = "34"
                            b {
                                +sourcesTitle
                            }
                            nbsp()
                            +sources
                        }
                    }
                }
                // linebreak

                div {
                    style = "page-break-after: always;"
                }

                // page 2
                table {
                    listWithTitle(humanAndEnvironmentDangerTitle, data.humanAndEnvironmentDanger)
                    listWithTitle(rulesOfConductTitle, data.rulesOfConduct)
                    listWithTitle(inCaseOfDangerTitle, data.inCaseOfDanger)
                    listWithTitle(disposalTitle, data.disposal)

                    tr("no-break") {
                        signatureBox(7, signature1, signature, locationAndDate)
                        signatureBox(5, signature2, signature, locationAndDate)
                    }
                }
            }
        }
        appendLine()
    }
}

private fun valueOrDash(value: String, unit: String = ""): String {
    return if (value.isBlank()) "-"
    else if (unit.isBlank()) value else value + nbsp + unit
}

private fun TD.nbsp() {
    +nbsp.toString()
}

private fun TABLE.header(title: String) {
    tr {
        th {
            colSpan = "34"
            +title
        }
    }
}

private fun TABLE.listWithTitle(title: String, list: List<String>) {
    val list = list.map { it.trim(); it.replace("\n", "") }
    tr("no-break") {
        td("top") {
            colSpan = "12"
            p("list-heading") {
                +title
            }
            ul {
                list.forEach {
                    li("list") {
                        +it
                    }
                }
            }
        }
    }
}

private fun TR.phrasesList(substances: List<Substance>, transform: (Substance) -> List<Pair<String, String>>) {
    td("top phrases") {
        colSpan = "17"
        val iterator = Substance.formatPhrases(substances, transform).iterator()
        while (iterator.hasNext()) {
            val (number, content) = iterator.next()
            +number
            +":"
            nbsp()
            +content
            if (iterator.hasNext()) br
        }
    }
}

private fun TR.textBlock(weight: Int, title: String, content: String? = null) {
    td {
        colSpan = weight.toString()
        +title
        if (content != null) {
            br
            +content
        }
    }
}

private fun TR.ingredientTitle(weight: Int, title: String) {
    td("center") {
        colSpan = weight.toString()
        val iterator = title.split("\n").iterator()
        while (iterator.hasNext()) {
            +iterator.next()
            if (iterator.hasNext()) br
        }
    }
}


private fun TR.signatureBox(weight: Int, signatureDescription: String, signature: String, locationAndDate: String) {
    td("top height-2cm td-signature") {
        colSpan = weight.toString()
        +signatureDescription
        div("signature-location-date") {
            span("signature") {
                +signature
            }
            span("location-date") {
                +locationAndDate
            }
        }
    }
}