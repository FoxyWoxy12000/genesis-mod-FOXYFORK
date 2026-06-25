package dev.d4vid.mods.genesis.server.config.data.pvp

import dev.d4vid.mods.genesis.server.config.serialization.NonNegativeDoubleSerializer
import dev.d4vid.mods.genesis.server.config.serialization.NonNegativeDurationSecondsDoubleSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class PvpDetectionConfig(
    @Serializable(with = NonNegativeDoubleSerializer::class)
    val minDamage: Double = 1.0,
    @Serializable(with = NonNegativeDurationSecondsDoubleSerializer::class)
    val damageTimeSecondsScaling: Duration = 10.seconds,
    @Serializable(with = NonNegativeDurationSecondsDoubleSerializer::class)
    val maxTimeSeconds: Duration = 30.seconds,
)
