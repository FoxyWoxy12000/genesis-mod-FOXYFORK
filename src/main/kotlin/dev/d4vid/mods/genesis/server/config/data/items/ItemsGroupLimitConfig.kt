package dev.d4vid.mods.genesis.server.config.data.items

import dev.d4vid.mods.genesis.server.config.serialization.NonNegativeIntSerializer
import kotlinx.serialization.Serializable
import net.minecraft.world.item.ItemStack

@Serializable
data class ItemsGroupLimitConfig(
    @Serializable(with = NonNegativeIntSerializer::class)
    val limit: Int,
    val items: List<ItemsGroupLimitItemConfig>,
) {
    fun getScalingForItem(stack: ItemStack): Int {
        return items.firstOrNull { it.match.matchItem(stack) }?.scaling ?: 0
    }
}
