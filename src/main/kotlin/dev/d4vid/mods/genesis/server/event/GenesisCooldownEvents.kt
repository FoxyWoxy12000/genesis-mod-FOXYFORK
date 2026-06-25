package dev.d4vid.mods.genesis.server.event

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantedItemInUse
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect

object GenesisCooldownEvents {
    val ALLOW_LUNGE = EventFactory.createArrayBacked(AllowLunge::class.java) { listeners ->
        AllowLunge { level, power, itemInUse, player, effect ->
            for (listener in listeners) {
                val result = listener.allowLunge(level, power, itemInUse, player, effect)

                if (!result) {
                    return@AllowLunge false
                }
            }

            true
        }
    }

    val ALLOW_ITEM_COOLDOWN = EventFactory.createArrayBacked(AllowItemCooldown::class.java) { listeners ->
        AllowItemCooldown { stack ->
            for (listener in listeners) {
                val result = listener.allowCooldown(stack)

                if (!result) {
                    return@AllowItemCooldown false
                }
            }

            true
        }
    }

    fun interface AllowLunge {
        fun allowLunge(
            level: ServerLevel,
            power: Int,
            itemInUse: EnchantedItemInUse,
            player: ServerPlayer,
            effect: EnchantmentEntityEffect
        ): Boolean
    }

    fun interface AllowItemCooldown {
        fun allowCooldown(stack: ItemStack): Boolean
    }
}
