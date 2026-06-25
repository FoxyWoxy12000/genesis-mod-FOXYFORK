package dev.d4vid.mods.genesis.server.config.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val ONE_SECOND_MILLIS = 1000.0
private const val ONE_MINUTE_SECONDS = 60.0

open class DurationSerializer<T>(
    private val serializer: KSerializer<T>,
    private val toValue: (duration: Duration) -> T,
    private val toDuration: (number: T) -> Duration,
) : KSerializer<Duration> {
    override val descriptor = SerialDescriptor("dev.d4vid.mods.genesis.Duration", serializer.descriptor)

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeSerializableValue(serializer, toValue(value))
    }

    override fun deserialize(decoder: Decoder): Duration {
        return toDuration(decoder.decodeSerializableValue(serializer))
    }
}

object DurationSecondsDoubleSerializer : DurationSerializer<Double>(
    Double.serializer(),
    { duration -> duration.inWholeMilliseconds / ONE_SECOND_MILLIS },
    { number -> (number * ONE_SECOND_MILLIS).milliseconds },
)

object DurationMinutesDoubleSerializer : DurationSerializer<Double>(
    Double.serializer(),
    { duration -> duration.inWholeSeconds / ONE_MINUTE_SECONDS },
    { number -> (number * ONE_MINUTE_SECONDS).seconds },
)
