package io.github.chaosdave34.benzol.files

import benzol.composeapp.generated.resources.*
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import io.github.chaosdave34.benzol.Substance
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import kotlin.text.Typography.nbsp

class HtmlFile(
    val documentTitle: String,
    val organisation: String,
    val course: String,
    val name: String,
    val place: String,
    val assistant: String,
    val preparation: String,
    val substanceList: List<Substance>,
    val humanAndEnvironmentDanger: List<String>,
    val rulesOfConduct: List<String>,
    val inCaseOfDanger: List<String>,
    val disposal: List<String>
) {
    @OptIn(ExperimentalResourceApi::class)
    suspend fun create(): String {
        val assistantTitle = getString(Res.string.assistant)
        val preparationTitle = getString(Res.string.preparation)
        val nameTitle = getString(Res.string.name_with_plural)
        val placeTitle = getString(Res.string.place)

        val usedSubstancesTitle = getString(Res.string.used_substances)
        val molarMassTitle = getString(Res.string.molar_mass_with_unit)
        val temperaturesTitle = getString(Res.string.temperatures)
        val ghsSymbolsTitle = getString(Res.string.ghs_pictograms)
        val hAndPPhrasesNumberTitle = getString(Res.string.h_and_p_phrases_number)
        val makLd50WgkTitle = getString(Res.string.mak_ld50_wgk)

        val quantityTitle = getString(Res.string.quantity_required)
        val makUnit = getString(Res.string.mak_unit)
        val lethalDoseUnit = getString(Res.string.lethal_dose_unit)
        val celsiusUnit = getString(Res.string.celsius_unit)

        val hAndPPhrasesTitle = getString(Res.string.title_h_and_p_phrases)
        val sourcesTitle = getString(Res.string.sources)
        val humanAndEnvironmentDangerTitle = getString(Res.string.human_and_environment_danger)
        val rulesOfConductTitle = getString(Res.string.rules_of_conduct)
        val inCaseOfDangerTitle = getString(Res.string.in_case_of_danger)
        val disposalTitle = getString(Res.string.disposal)

        val signature = getString(Res.string.signature)
        val locationAndDate = getString(Res.string.location_and_date)
        val signature1 = getString(Res.string.signature_1)
        val signature2 = getString(Res.string.signature_2)

        val css = Res.readBytes("files/export.css").decodeToString()

        val html = buildString {
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
                        header(documentTitle)
                        header(organisation)
                        header(course)
                        tr {
                            textBlock(13, nameTitle, name)
                            textBlock(8, placeTitle, place)
                            textBlock(13, assistantTitle, assistant)
                        }
                        tr {
                            td {
                                colSpan = "34"
                                +preparationTitle
                                br
                                b {
                                    +preparation
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
                        substanceList.forEach { substance ->
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
                        tr("full-height no-break") {
                            phrasesList { it.hPhrases }
                            phrasesList { it.pPhrases }
                        }
                        tr {
                            td {
                                colSpan = "34"
                                b {
                                    +sourcesTitle
                                }
                                nbsp()
                                +Substance.formatSource(substanceList)
                            }
                        }
                    }
                    // linebreak

                    div {
                        style = "page-break-after: always;"
                    }

                    // page 2
                    table {
                        listWithTitle(humanAndEnvironmentDangerTitle, humanAndEnvironmentDanger)
                        listWithTitle(rulesOfConductTitle, rulesOfConduct)
                        listWithTitle(inCaseOfDangerTitle, inCaseOfDanger)
                        listWithTitle(disposalTitle, disposal)

                        tr("no-break") {
                            signatureBox(7, signature1, signature, locationAndDate)
                            signatureBox(5, signature2, signature, locationAndDate)
                        }
                    }
                }
            }
            appendLine()
        }


        val document = Ksoup.parse(html)
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        return document.html()
    }

    private fun valueOrDash(value: String, unit: String = ""): String {
        return if (value.isBlank()) "-"
        else if (unit.isBlank()) value else value + nbsp + unit
    }

    fun TD.nbsp() {
        +nbsp.toString()
    }

    fun TABLE.header(title: String) {
        tr {
            th {
                colSpan = "34"
                +title
            }
        }
    }

    fun TABLE.listWithTitle(title: String, list: List<String>) {
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

    fun TR.phrasesList(transform: (Substance) -> List<Pair<String, String>>) {
        td("full-height top phrases") {
            colSpan = "17"
            val iterator = Substance.formatPhrases(substanceList, transform).iterator()
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

    fun TR.textBlock(weight: Int, title: String, content: String? = null) {
        td {
            colSpan = weight.toString()
            +title
            if (content != null) {
                br
                +content
            }
        }
    }

    fun TR.ingredientTitle(weight: Int, title: String) {
        td("center") {
            colSpan = weight.toString()
            val iterator = title.split("\n").iterator()
            while (iterator.hasNext()) {
                +iterator.next()
                if (iterator.hasNext()) br
            }
        }
    }


    fun TR.signatureBox(weight: Int, signatureDescription: String, signature: String, locationAndDate: String) {
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
}