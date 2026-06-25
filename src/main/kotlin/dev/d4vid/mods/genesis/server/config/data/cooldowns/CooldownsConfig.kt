package dev.d4vid.mods.genesis.server.config.data.cooldowns

import kotlinx.serialization.Serializable
import net.minecraft.world.item.ItemStack
import kotlin.time.Duration.Companion.seconds

@Serializable
data class CooldownsConfig(
    val lunge: CooldownsCustomConfig = CooldownsCustomConfig(inCombat = 5.seconds),
    private val items: List<CooldownsItemConfig> = listOf(),
) {
    fun getCooldownForItem(stack: ItemStack): CooldownsItemConfig? {
        return items.firstOrNull { it.match.matchItem(stack) }
    }
}
