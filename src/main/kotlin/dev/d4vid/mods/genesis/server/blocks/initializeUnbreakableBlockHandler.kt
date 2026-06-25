package dev.d4vid.mods.genesis.server.blocks

import dev.d4vid.mods.genesis.server.config.GenesisConfig
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.minecraft.world.level.GameType

fun initializeUnbreakableBlockHandler(config: GenesisConfig) {
    PlayerBlockBreakEvents.BEFORE.register { _, player, _, state, _ ->
        player.gameMode() == GameType.CREATIVE || !config.data.blocks.isUnbreakable(state)
    }
}
