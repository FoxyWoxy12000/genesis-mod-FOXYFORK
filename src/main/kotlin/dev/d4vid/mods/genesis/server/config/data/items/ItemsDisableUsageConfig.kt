package dev.d4vid.mods.genesis.server.config.data.items

import dev.d4vid.mods.genesis.server.config.serialization.ItemMatcher
import kotlinx.serialization.Serializable

@Serializable
data class ItemsDisableUsageConfig(
    val match: ItemMatcher,
    val inCombat: Boolean = false,
)
