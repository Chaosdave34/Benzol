package io.github.chaosdave34.benzol.data

import benzol.composeapp.generated.resources.Res
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object Statements {
    private val json = Json { ignoreUnknownKeys = true }
    lateinit var hStatements: Map<String, String>
    lateinit var pStatements: Map<String, String>

    suspend fun load() {
        val value = Res.readBytes("files/hpstatements-de-latest.json")
        val statementsFile: StatementsFile = json.decodeFromString(value.decodeToString())
        val statements = statementsFile.statements.mapKeys { it.key.removePrefix("latest/de/") }

        hStatements = statements.filterKeys { it.startsWith("H") }
        pStatements = statements.filterKeys { it.startsWith("P") }
    }

    @Serializable
    private data class StatementsFile(
        val statements: Map<String, String>
    )
}