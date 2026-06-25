package dev.d4vid.mods.genesis.server.config.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

open class NonNegativeSerializer<T>(
    private val serializer: KSerializer<T>,
    private val zero: T
) : KSerializer<T>
    where T : Comparable<T> {
    override val descriptor = SerialDescriptor("dev.d4vid.mods.genesis.NonNegative", serializer.descriptor)

    override fun serialize(encoder: Encoder, value: T) {
        ensureNonNegative(value)

        encoder.encodeSerializableValue(serializer, value)
    }

    override fun deserialize(decoder: Decoder): T {
        val value = decoder.decodeSerializableValue(serializer)

        ensureNonNegative(value)

        return value
    }

    private fun ensureNonNegative(value: T) {
        require(value >= zero) { "Value must be non-negative" }
    }
}

object NonNegativeIntSerializer : NonNegativeSerializer<Int>(Int.serializer(), 0)
object NonNegativeDoubleSerializer : NonNegativeSerializer<Double>(Double.serializer(), 0.0)

object NonNegativeDurationSecondsDoubleSerializer : NonNegativeSerializer<Duration>(
    DurationSecondsDoubleSerializer,
    0.seconds,
)

object NonNegativeDurationMinutesDoubleSerializer : NonNegativeSerializer<Duration>(
    DurationMinutesDoubleSerializer,
    0.minutes,
)
