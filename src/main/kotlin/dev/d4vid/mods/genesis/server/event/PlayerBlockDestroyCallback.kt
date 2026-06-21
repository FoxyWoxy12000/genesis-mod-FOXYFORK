package dev.d4vid.mods.genesis.server.event

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player

fun interface PlayerBlockDestroyCallback {
    companion object {
        val EVENT = EventFactory.createArrayBacked(PlayerBlockDestroyCallback::class.java) { listeners ->
            PlayerBlockDestroyCallback { player, blockPos ->
                for (listener in listeners) {
                    val result = listener.interact(player, blockPos)

                    if (result != InteractionResult.PASS) {
                        return@PlayerBlockDestroyCallback result
                    }
                }

                InteractionResult.PASS
            }
        }
    }

    fun interact(player: Player, blockPos: BlockPos): InteractionResult
}
