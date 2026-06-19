package dev.d4vid.mods.genesis.server.event

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerPlayer

fun interface PlayerInventoryInteractCallback {
    companion object {
        val EVENT = EventFactory.createArrayBacked(PlayerInventoryInteractCallback::class.java) { listeners ->
            PlayerInventoryInteractCallback { player ->
                for (listener in listeners) {
                    listener.interact(player)
                }
            }
        }
    }

    fun interact(player: ServerPlayer)
}
