package dev.d4vid.mods.genesis.server.config.data.items

import dev.d4vid.mods.genesis.server.config.serialization.ItemMatcher
import dev.d4vid.mods.genesis.server.config.serialization.NonNegativeIntSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ItemsLimitConfig(
    @Serializable(with = NonNegativeIntSerializer::class)
    val limit: Int,
    val match: ItemMatcher,
)
