package dev.d4vid.mods.genesis.server.config.serialization

import dev.d4vid.mods.genesis.server.config.field.NbtMatcher
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier

open class NbtMatcherSerializer(private val registry: Registry<*>) : KSerializer<NbtMatcher> {
    override val descriptor = buildClassSerialDescriptor("dev.d4vid.mods.genesis.NbtMatcher") {
        element<String>("identifier")
        element<JsonElement?>("nbt")
    }

    override fun serialize(
        encoder: Encoder,
        value: NbtMatcher
    ) {
        ensureRegistry(value)

        if (value.nbt == null) {
            encoder.encodeString(value.identifier.toString())
        } else {
            encoder.encodeSerializableValue(NbtMatcher.serializer(), value)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): NbtMatcher {
        require(decoder is JsonDecoder) { "NbtMatcherSerializer requires JSON" }

        return when (val element = decoder.decodeJsonElement()) {
            is JsonObject -> Json.decodeFromJsonElement(NbtMatcher.serializer(), element)
            is JsonPrimitive if element.isString -> NbtMatcher(Identifier.parse(element.content))
            else -> throw SerializationException("Invalid NBT matcher format")
        }
    }

    private fun ensureRegistry(material: NbtMatcher) {
        require(registry.containsKey(material.identifier)) { "Value must be in the ${registry.key()} registry." }
    }
}

object ItemSerializer : NbtMatcherSerializer(BuiltInRegistries.ITEM)
object BlockSerializer : NbtMatcherSerializer(BuiltInRegistries.BLOCK)

typealias ItemMatcher = @Serializable(with = ItemSerializer::class) NbtMatcher
typealias BlockMatcher = @Serializable(with = BlockSerializer::class) NbtMatcher
