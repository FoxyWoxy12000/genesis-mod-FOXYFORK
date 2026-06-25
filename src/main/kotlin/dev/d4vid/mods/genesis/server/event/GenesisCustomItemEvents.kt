package dev.d4vid.mods.genesis.server.event

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

object GenesisCustomItemEvents {
    val ALLOW_ITEM_SWAP = EventFactory.createArrayBacked(AllowItemSwap::class.java) { listeners ->
        AllowItemSwap { player, stack ->
            for (listener in listeners) {
                val result = listener.allowItemSwap(player, stack)

                if (!result) {
                    return@AllowItemSwap false
                }
            }
            true
        }
    }

    fun interface AllowItemSwap {
        fun allowItemSwap(player: ServerPlayer, stack: ItemStack): Boolean
    }
}
