package dev.d4vid.mods.genesis.server.event

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult

fun interface PlayerItemUseCallback {
    companion object {
        val EVENT = EventFactory.createArrayBacked(PlayerItemUseCallback::class.java) { listeners ->
            PlayerItemUseCallback { player, level, stack, hand, blockHit ->
                for (listener in listeners) {
                    val result = listener.interact(player, level, stack, hand, blockHit)

                    if (result != InteractionResult.PASS) {
                        return@PlayerItemUseCallback result
                    }
                }

                InteractionResult.PASS
            }
        }
    }

    fun interact(
        player: ServerPlayer,
        level: Level,
        stack: ItemStack,
        hand: InteractionHand,
        blockHit: BlockHitResult?
    ): InteractionResult
}
