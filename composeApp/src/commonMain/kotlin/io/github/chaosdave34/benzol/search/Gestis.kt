package io.github.chaosdave34.benzol.search

import benzol.composeapp.generated.resources.*
import com.fleeksoft.ksoup.Ksoup
import io.github.chaosdave34.benzol.data.*
import io.github.chaosdave34.benzol.data.Wgk.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

private const val TOKEN = "dddiiasjhduuvnnasdkkwUUSHhjaPPKMasd" // don't ask, just leave it (https://gestis.dguv.de/search)

object Gestis { // TODO add tests
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url("https://gestis-api.dguv.de/api/")
        }
    }

    suspend fun search(
        search: List<Pair<SearchType, String>>,
        exact: Boolean
    ): List<SearchResult>? {
        val response = try {
            client.get("search/de") {
                search.forEach { (searchType, value) ->
                    parameter(searchType.parameterName, value)
                }
                parameter("exact", exact)
            }
        } catch (_: Throwable) {
            return null
        }

        return if (response.status == HttpStatusCode.OK) {
            val searchResults: List<SearchResult> = response.body()

            searchResults.sortedBy { it.rank }
        } else null
    }

    suspend fun getSearchSuggestions(searchType: SearchType, value: String): List<String> {
        val response = try {
            client.get("search_suggestions/de") {
                parameter(searchType.parameterName, value)
            }
        } catch (_: Throwable) {
            return emptyList()
        }

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else emptyList()

    }

    suspend fun getSubstanceInformation(search: SearchResult): SubstanceInformation? {
        val response = try {
            client.get("article/de/${search.zgvNumber}") {
                header("Authorization", "Bearer $TOKEN")
            }
        } catch (_: Throwable) {
            return null
        }

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else null
    }

    enum class SearchType(override val label: StringResource, val parameterName: String) : Labeled {
        ChemicalName(Res.string.chemical_name, "stoffname"),
        CasNumber(Res.string.number, "nummern"),
        MolecularFormula(Res.string.molecular_formula, "summenformel"),
        FullText(Res.string.full_text, "volltextsuche")
    }

    @Serializable
    data class SearchResult(
        @SerialName("zvg_nr") val zgvNumber: String,
        val rank: String,
        @SerialName("cas_nr") val casNumber: String?,
        val name: String
    )

    @Serializable
    data class SubstanceInformation(
        @SerialName("zvgnummer") val zvgNumber: String,
        @SerialName("zvgnummer_mit_null") val zvgNumberWithZeros: String,
        val name: String,
        @SerialName("sortiername") val sortName: String,
        val aliases: List<Alias>,
        @SerialName("hauptkapitel") val mainChapter: List<MainChapter>
    ) {
        fun getSubstance(): Substance {
            return Substance(
                name,
                getCasNumber(),
                getMolecularFormula(),
                getWgk(),
                getSignalWorld(),
                getMolarMass(),
                getLethalDose(),
                getMak(),
                getMakUnit(),
                getMeltingPoint(),
                getBoilingPoint(),
                getDecompositionTemperature(),
                getHazardStatements(),
                getPrecautionaryStatements(),
                getGHSPictograms(),
                Pair(Source.Gestis, "https://gestis.dguv.de/data?name=$zvgNumberWithZeros")
            )
        }

        private fun getCasNumber(): String {
            val chapter = getChapter("0100", "0100")

            val casNumber = Ksoup.parseXml(chapter).selectFirst("casnr")?.text() ?: ""
            return casNumber.trim()
        }

        private fun getMolecularFormula(): String {
            val chapter = getChapter("0400", "0400")

            val molecularFormula = Ksoup.parseXml(chapter).selectFirst("summenformel")?.html() ?: ""

            val pattern = molecularFormula
                .split("<br />")
                .first()
                .map { Regex.escape(it.toString()) + "(?:<sub>|</sub>)?" }
                .joinToString("")
            val match = "<td align=\"left\">(?<formula>$pattern)(?:<br />.*?)*?</td>".toRegex().find(chapter)

            return match?.groups["formula"]?.value?.replace("<sub>", "<")?.replace("</sub>", ">") ?: ""

        }

        private fun getWgk(): Wgk {
            val chapter = getChapter("1100", "1106")

            val wgk = Ksoup.parseXml(chapter).selectFirst("td:matches(WGK [1-3])")?.text() ?: ""
            val match = "WGK (?<number>[1-3])".toRegex().find(wgk)
            val number = match?.groups["number"]?.value?.toIntOrNull()

            return when (number) {
                1 -> WGK_1
                2 -> WGK_2
                3 -> WGK_3
                else -> NONE
            }
        }

        private fun getSignalWorld(): SignalWord {
            val chapter = getChapter("1100", "1303")

            val signalWord = Ksoup.parseXml(chapter).selectFirst("td:has(b:contains(Signalwort)) + td")?.text() ?: ""

            return when (signalWord.replace("\"", "").trim()) {
                "Achtung" -> SignalWord.WARNING
                "Gefahr" -> SignalWord.DANGER
                else -> SignalWord.NONE
            }
        }

        private fun getMolarMass(): String {
            val chapter = getChapter("0400", "0400")

            val molarMass = Ksoup.parseXml(chapter).selectFirst("td:has(b:contains(Molare Masse)) + td")?.text() ?: ""

            return molarMass.removeSuffix("g/mol").trim()
        }

        private fun getLethalDose(): String { // TODO limited to LD50 oral Ratte with unit mg/kg
            val chapter = getChapter("0500", "0501")

            val lethalDose = Ksoup.parseXml(chapter).selectFirst("table:has(tr > td:contains(LD50 oral Ratte)) + table > tr > td:matches(\\d)")?.text() ?: ""

            return lethalDose.removeSuffix("mg/kg").trim()
        }

        private fun getMak(): String {
            val chapter = getChapter("1100", "1203")

            val makValues = Ksoup.parseXml(chapter)
                .select("table:has(tr:has(td:contains(Die Angaben sind wissenschaftliche Empfehlungen und kein geltendes Recht.))) + table + table td:matches(\\d)")
                .eachText()

            return makValues.firstOrNull()
                ?.replace("m[gl]/m³".toRegex(), "")
                ?.trim()
                ?: ""
        }

        private fun getMakUnit(): Substance.MakUnit {// todo eventually join with getMak to a single getter
            val chapter = getChapter("1100", "1203")

            val makValue = Ksoup.parseXml(chapter)
                .selectFirst("table:has(tr:has(td:contains(Die Angaben sind wissenschaftliche Empfehlungen und kein geltendes Recht.))) + table + table td:matches(\\d)")
                ?.text() ?: ""

            return if (makValue.contains("ml/m³")) Substance.MakUnit.MILLI_LITRE_PER_CUBIC_METRE else Substance.MakUnit.MILLI_GRAM_PER_CUBIC_METRE
        }

        private fun getMeltingPoint(): String { // °C can be C° sometimes, see https://gestis.dguv.de/data?name=520030&lang=de
            val chapter = getChapter("0600", "0602")

            val meltingPoint = Ksoup.parseXml(chapter).selectFirst("td:contains(Schmelzpunkt) + td")?.text() ?: ""

            return meltingPoint.replace("°C|C°".toRegex(), "").trim()
        }

        private fun getBoilingPoint(): String { // °C can be C° sometimes, see https://gestis.dguv.de/data?name=520030&lang=de
            val chapter = getChapter("0600", "0603")

            val meltingPoint = Ksoup.parseXml(chapter).selectFirst("td:contains(Siedepunkt) + td")?.text() ?: ""

            return meltingPoint.replace("°C|C°".toRegex(), "").trim()
        }

        private fun getDecompositionTemperature(): String {
            val chapter = getChapter("0600", "0619")

            val decompositionTemperature = Ksoup.parseXml(chapter).selectFirst("td:contains(Zersetzungstemperatur) + td")?.text() ?: ""

            return decompositionTemperature.replace("°C|C°".toRegex(), "").trim()
        }

        private fun getHazardStatements(): List<Pair<String, String>> {
            val chapter = getChapter("1100", "1303")

            val hazardStatements = Ksoup.parseXml(chapter).selectFirst("tr:has(b:matches(\\bH-Sätze)) + tr > td")?.html() ?: ""

            val regex = "(?:^|(?<=</verstecktercode>|<br />))(?<number>H\\d{3}.*?): (?<statement>.+?)(?=<br />(?!-| +<verstecktercode>)|\\z)".toRegex()
            val additionalInfoRegex = "<verstecktercode>(?<number>H\\d{3}.*?)</verstecktercode>-+ (?<info>.+?)(?:<br />|$)".toRegex()

            val additionalInfoText = hazardStatements.replace(regex, "").trim()
            val additionalInfo = additionalInfoRegex.findAll(additionalInfoText).groupBy(
                keySelector = { it.groups["number"]?.value ?: "" },
                valueTransform = { it.groups["info"]?.value ?: "" }
            ).mapValues { it.value.joinToString(" ", transform = String::trim, prefix = " ") }

            return regex.findAll(hazardStatements)
                .map {
                    val number = it.groups["number"]?.value ?: ""
                    val statement = it.groups["statement"]?.value ?: ""

                    Pair(
                        number,
                        statement.replace("<br />(?: *<verstecktercode>.+?</verstecktercode>)?-+".toRegex(), "").trim() +
                                additionalInfo.getOrElse(number) { "" }
                    )
                }.toList()
        }

        private fun getPrecautionaryStatements(): List<Pair<String, String>> {
            val chapter = getChapter("1100", "1303")

            val precautionaryStatements = Ksoup.parseXml(chapter).selectFirst("tr:has(b:matches(P-Sätze)) + tr > td")?.html() ?: ""

            val regex = "(?<number>P\\d.*?): (?<statement>.+)".toRegex()
            return precautionaryStatements
                .split("<br />")
                .mapNotNull(regex::find)
                .map {
                    val number = it.groups["number"]?.value ?: ""
                    val statement = it.groups["statement"]?.value ?: ""

                    Pair(number, statement)
                }
        }

        private fun getGHSPictograms(): Set<GHSPictogram> {
            val chapter = getChapter("1100", "1303")

            val ghsPictograms = Ksoup.parseXml(chapter).select("td > img[alt~=ghs]").eachAttr("src")

            return ghsPictograms
                .map { "ghs0(?<number>[1-9])".toRegex().find(it)?.groups["number"]?.value?.toIntOrNull() }
                .mapNotNull {
                    when (it) {
                        1 -> GHSPictogram.GHS_001
                        2 -> GHSPictogram.GHS_002
                        3 -> GHSPictogram.GHS_003
                        4 -> GHSPictogram.GHS_004
                        5 -> GHSPictogram.GHS_005
                        6 -> GHSPictogram.GHS_006
                        7 -> GHSPictogram.GHS_007
                        8 -> GHSPictogram.GHS_008
                        9 -> GHSPictogram.GHS_009
                        else -> null
                    }
                }.toSet()
        }

        private fun getChapter(main: String, sub: String): String {
            return mainChapter
                .firstOrNull { it.drnr == main }
                ?.subChapter
                ?.firstOrNull { it.drnr == sub }
                ?.text
                ?: ""
        }

    }

    @Serializable
    data class Alias(
        val name: String,
        @SerialName("sortiername") val sortName: String,
    )

    @Serializable
    data class MainChapter(
        val drnr: String,
        @SerialName("ueberschrift") val header: String?, // XML
        @SerialName("unterkapitel") val subChapter: List<SubChapter>,
        val links: String? = null, // XML
        val tables: String? = null // XML
    )

    @Serializable
    data class SubChapter(
        val drnr: String,
        val text: String? // XML
    )
}