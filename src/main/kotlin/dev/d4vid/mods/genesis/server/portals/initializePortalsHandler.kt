package dev.d4vid.mods.genesis.server.portals

import dev.d4vid.mods.genesis.server.config.GenesisConfig
import dev.d4vid.mods.genesis.server.event.GenesisPortalEvents
import net.minecraft.world.level.Level

fun initializePortalsHandler(config: GenesisConfig) {
    GenesisPortalEvents.ALLOW_NETHER.register { level ->
        level == Level.NETHER || config.data.portals.allowNether
    }

    GenesisPortalEvents.ALLOW_END.register { level ->
        level == Level.END || config.data.portals.allowEnd
    }
}
