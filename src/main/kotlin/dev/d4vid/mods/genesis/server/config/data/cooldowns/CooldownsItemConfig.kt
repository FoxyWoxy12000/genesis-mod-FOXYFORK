package dev.d4vid.mods.genesis.server.config.data.cooldowns

import dev.d4vid.mods.genesis.server.config.serialization.ItemMatcher
import dev.d4vid.mods.genesis.server.config.serialization.NonNegativeDurationSecondsDoubleSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class CooldownsItemConfig(
    val match: ItemMatcher,
    @Serializable(with = NonNegativeDurationSecondsDoubleSerializer::class)
    val global: Duration = 0.seconds,
    @Serializable(with = NonNegativeDurationSecondsDoubleSerializer::class)
    val inCombat: Duration = 0.seconds,
)
