package dev.d4vid.mods.genesis.server.event

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.item.ItemStack

object GenesisRecipeEvents {
    val ALLOW = EventFactory.createArrayBacked(Allow::class.java) { listeners ->
        Allow { input, result ->
            for (listener in listeners) {
                val result = listener.allow(input, result)

                if (!result) {
                    return@Allow false
                }
            }

            true
        }
    }

    fun interface Allow {
        fun allow(input: Array<ItemStack>, result: ItemStack): Boolean
    }
}
