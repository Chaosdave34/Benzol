package io.github.chaosdave34.benzol.files.export

import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.files.CaBr2File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object FileUtils {
    const val FILE_EXTENSION = "benzol"

    fun getVersion(savable: Savable) = when (savable) {
        is CaBr2File.CaBr2Data -> 0
        is SafetySheetInputState -> 1
        else -> error("Unknown savable type")
    }

    private val module = SerializersModule {
        polymorphic(Savable::class) {
            subclass(CaBr2File.CaBr2Data::class)
            subclass(SafetySheetInputState::class)
        }
    }

    private val format = Json {
        serializersModule = module
        ignoreUnknownKeys = true
    }

    @Serializable(with = FileSerializer::class)
    data class File(
        val version: Int,
        val content: Savable
    )


    fun Savable.encode(): String {
        val export = File(getVersion(this), this)
        return format.encodeToString(export)
    }

    fun decode(value: String): Savable? {
        // First check benzol file
        try {
            return format.decodeFromString<File>(value).content
        } catch (e: Exception) {
            println(e)
        }

        // Second check CaBr2 file
        try {
            return Json.decodeFromString<CaBr2File.CaBr2Data>(value)
        } catch (e: Exception) {
            println(e)
        }

        // Return null as no file matched
        return null
    }
}