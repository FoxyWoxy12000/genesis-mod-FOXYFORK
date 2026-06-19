package dev.d4vid.mods.genesis.server.serialization

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder

object GsonElementSerializer : KSerializer<JsonElement> {
    override val descriptor = buildClassSerialDescriptor("GsonElementSerializer")

    override fun serialize(encoder: Encoder, value: JsonElement) {
        val jsonEncoder = encoder as? JsonEncoder ?: error("This serializer can only be used with JSON")

        jsonEncoder.encodeJsonElement(Json.parseToJsonElement(value.toString()))
    }

    override fun deserialize(decoder: Decoder): JsonElement {
        val jsonDecoder = decoder as? JsonDecoder ?: error("This deserializer can only be used with JSON")

        return JsonParser.parseString(jsonDecoder.decodeJsonElement().toString())
    }
}
