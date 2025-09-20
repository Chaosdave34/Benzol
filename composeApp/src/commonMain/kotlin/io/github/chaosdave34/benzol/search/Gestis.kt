package io.github.chaosdave34.benzol.search

import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.GHSPictogram
import io.github.chaosdave34.benzol.Substance
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

private const val BASE_URL = "https://gestis-api.dguv.de/api"
private const val TOKEN = "dddiiasjhduuvnnasdkkwUUSHhjaPPKMasd" // don't ask, just leave it (https://gestis.dguv.de/search)

object Gestis {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun search(search: Search): List<SearchResult>? {
        val parameters = search.search.joinToString("&") { "${it.searchType.parameterName}=${it.value}" }
        val url = "$BASE_URL/search/de?$parameters&exact=${search.exact}"

        val response = client.get(url)

        return if (response.status == HttpStatusCode.OK) {
            val searchResults: List<SearchResult> = response.body()

            searchResults.sortedBy { it.rank }
        } else null
    }

    suspend fun getSearchSuggestions(search: SearchArgument): List<String> {
        val url = "$BASE_URL/search_suggestions/de?${search.searchType.parameterName}=${search.value}"

        val response = client.get(url)

        return if (response.status == HttpStatusCode.OK) {
            return response.body()
        } else emptyList()

    }

    suspend fun getSubstanceInformation(search: SearchResult): SubstanceInformation? {
        val url = "$BASE_URL/article/de/${search.zgvNumber}"

        val response = client.get(url) {
            header("Authorization", "Bearer $TOKEN")
        }

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else null
    }

    data class Search(
        val search: List<SearchArgument>,
        val exact: Boolean
    )

    data class SearchArgument(
        var searchType: SearchType,
        var value: String,
    )

    enum class SearchType(val stringResource: StringResource, val parameterName: String) {
        ChemicalName(Res.string.chemical_name, "stoffname"),
        MolecularFormula(Res.string.molecular_formula, "summenformel"),
        CasNumber(Res.string.number, "nummern"),
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

            return Substance.fromSource(
                name,
                getCas(),
                getMolecularFormula(),
                getFormatedMolecularFormula(),
                getWgk(),
                getSignalWorld(),
                getMolarMass(),
                getLethalDose(),
                getMak(),
                getMeltingPoint(),
                getBoilingPoint(),
                getHPhrases(),
                getPPhrases(),
                getGHSPictograms(),
                Pair(Source.Gestis, "https://gestis.dguv.de/data?name=$zvgNumberWithZeros")
            )
        }

        private fun getCas(): String {
            val chapter = getChapter("0100", "0100").getContent()

            // Filter for first regex match
            val match = "<casnr>(?<cas>[0-9-]+)</casnr>".toRegex().find(chapter)
            return match?.groups["cas"]?.value ?: ""
        }

        private fun getMolecularFormula(): String {
            val chapter = getChapter("0400", "0400").getContent()

            val match = "<summenformel>(?<formula>[0-9A-Za-z()* ]+?)(?:<br />[0-9A-Za-z()* ]+?)*</summenformel>".toRegex().find(chapter)
            return match?.groups["formula"]?.value ?: ""
        }

        private fun getFormatedMolecularFormula(): String {
            val chapter = getChapter("0400", "0400").getContent()

            val pattern = getMolecularFormula().map { Regex.escape(it.toString()) + "(?:<sub>|</sub>)?" }.joinToString("")

            val match = "<td align=\"left\">(?<formula>$pattern)(?:<br />.*?)*?</td>".toRegex().find(chapter)
            return match?.groups["formula"]?.value?.replace("<sub>", "<")?.replace("</sub>", ">") ?: ""
        }

        private fun getWgk(): String {
            val chapter = getChapter("1100", "1106").getContent()

            val match = "<td align=\"left\">(?<wgk>WGK [1-3]) {2}- {2}[a-z]+ wassergefährdend</td>".toRegex().find(chapter)
            return match?.groups["wgk"]?.value ?: ""
        }

        private fun getSignalWorld(): String {
            val chapter = getChapter("1100", "1303").getContent()

            val match = "<td class=\"vortext\" align=\"left\"><b>Signalwort:</b></td>\\n *<td>\"(?<word>[a-zA-Z]+)\"</td>".toRegex().find(chapter)
            return match?.groups["word"]?.value ?: ""
        }

        private fun getMolarMass(): String {
            val chapter = getChapter("0400", "0400").getContent()

            val match = "<b>Molare Masse:</b></td>\\n *<td> ?(?<molarMass>[0-9,]+) ?g/mol</td>".toRegex().find(chapter)
            return match?.groups["molarMass"]?.value ?: ""
        }

        private fun getLethalDose(): String {
            val chapter = getChapter("0500", "0501").getContent()

            // 1. Check if exist
            if (!chapter.contains("LD50 oral Ratte")) return ""

            // 2. Split at title
            val part = chapter.split("LD50 oral Ratte").last()

            // 3. Get first table
            val table = "<table class=\"block\">(?:.*\\n)+? *</table>".toRegex().find(part)?.value ?: return ""

            // 4. Find value
            val match = "<td align=\"left\"> (?<ld50>[0-9,]+) mg/kg</td>".toRegex().find(table)

            return match?.groups["ld50"]?.value ?: ""
        }

        private fun getMak(): String {
            val chapter = getChapter("1100", "1203").getContent()

            // 1. Check if exist
            if (!chapter.contains("Die Angaben sind wissenschaftliche Empfehlungen und kein geltendes Recht.")) return ""

            // 2. Split at title
            val part = chapter.split("Die Angaben sind wissenschaftliche Empfehlungen und kein geltendes Recht.").last()

            // 3. Get first table
            val table = "<table class=\"block\">(?:.*\\n)+? *</table>".toRegex().find(part)?.value ?: return ""

            // 4. Find value
            val match = "<td align=\"left\"> (?<mak>[0-9,.]+) (?:mg/m³|ppm)</td>".toRegex().find(table)

            return match?.groups["mak"]?.value ?: ""
        }

        private fun getMeltingPoint(): String { // °C can be C° sometimes, see https://gestis.dguv.de/data?name=520030&lang=de
            val chapter = getChapter("0600", "0602").getContent()

            val match = "<td class=\"vortext\" align=\"left\">Schmelzpunkt:</td>\\n *<td>(?<point>[0-9,-]+) (?:°C|C°)</td>".toRegex().find(chapter)
            return match?.groups["point"]?.value ?: ""
        }

        private fun getBoilingPoint(): String { // °C can be C° sometimes, see https://gestis.dguv.de/data?name=520030&lang=de
            val chapter = getChapter("0600", "0603").getContent()

            val match = "<td class=\"vortext\" align=\"left\">Siedepunkt:</td>\\n *<td>(?<point>[0-9,-]+) (?:°C|C°)</td>".toRegex().find(chapter)
            return match?.groups["point"]?.value ?: ""
        }

        private fun getHPhrases(): List<Pair<String, String>> {
            val chapter = getChapter("1100", "1303").getContent()

            val matches = ">(?<number>H[0-9]{3}(?:\\+H[0-9]{3})*): (?<phrase>.+?\\.)(?=<br />|</td>)".toRegex().findAll(chapter)

            return matches.map { Pair(it.groups["number"]?.value ?: "", it.groups["phrase"]?.value ?: "") }.toList()
        }

        private fun getPPhrases(): List<Pair<String, String>> {
            val chapter = getChapter("1100", "1303").getContent()

            val matches = ">(?<number>P[0-9]{3}(?:\\+P[0-9]{3})*): (?<phrase>.+?\\.)(?=<br />|</td>)".toRegex().findAll(chapter)

            return matches.map { Pair(it.groups["number"]?.value ?: "", it.groups["phrase"]?.value ?: "") }.toList()
        }

        private fun getGHSPictograms(): List<GHSPictogram> {
            val chapter = getChapter("1100", "1303").getContent()

            val matches =
                "<img src=\"https://gestis-api\\.dguv\\.de/api/exactimage/GHS/(?<ghs>ghs0[1-9])\\.gif\" alt=\"ghs0[1-9]\" />".toRegex().findAll(chapter)


            return matches.mapNotNull { GHSPictogram.fromId(it.groups["ghs"]?.value ?: "") }.toList()
        }

        private fun getChapter(main: String, sub: String): SubChapter? {
            val mainChapter = mainChapter.firstOrNull { it.drnr == main } ?: return null
            return mainChapter.subChapter.firstOrNull { it.drnr == sub }
        }

        private fun SubChapter?.getContent(): String {
            val content = this?.text ?: return ""
            content.replace("\n", "")
            return content
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