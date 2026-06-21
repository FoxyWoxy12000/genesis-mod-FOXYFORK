package dev.d4vid.mods.genesis.server.block

import dev.d4vid.mods.genesis.server.GenesisConfig
import dev.d4vid.mods.genesis.server.event.PlayerBlockDestroyCallback
import net.minecraft.world.InteractionResult

fun registerBlockDestroyPrevention() {
    PlayerBlockDestroyCallback.EVENT.register { player, blockPos ->
        val blockState = player.level().getBlockState(blockPos)

        if (GenesisConfig.isBlockUnbreakable(blockState.block)) {
            InteractionResult.FAIL
        } else {
            InteractionResult.PASS
        }
    }
}
