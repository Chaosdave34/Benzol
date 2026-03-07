package io.github.chaosdave34.benzol.files.export

import io.github.chaosdave34.benzol.data.SafetySheetInputState
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

object FileSerializer : KSerializer<FileUtils.File> {
    override val descriptor = buildClassSerialDescriptor("File") {
        element<Int>("version")
        element<JsonElement>("content")
    }

    override fun serialize(encoder: Encoder, value: FileUtils.File) {
        val jsonEncoder = encoder as? JsonEncoder ?: error("only JSON is supported")

        val contentElement = when (value.content) {
            is SafetySheetInputState -> jsonEncoder.json.encodeToJsonElement(value.content)
            else -> error("Unknown savable type")
        }

        val obj = buildJsonObject {
            put("version", value.version)
            put("content", contentElement)
        }

        jsonEncoder.encodeJsonElement(obj)
    }

    override fun deserialize(decoder: Decoder): FileUtils.File {
        val jsonDecoder = decoder as? JsonDecoder ?: error("only JSON is supported")

        val obj = jsonDecoder.decodeJsonElement().jsonObject
        val version = obj["version"]!!.jsonPrimitive.int
        val contentJson = obj["content"]!!

        val content: Savable = when (version) {
            1 -> jsonDecoder.json.decodeFromJsonElement<SafetySheetInputState>(contentJson)
            else -> error("Unknown version $version")
        }

        return FileUtils.File(version, content)
    }
}