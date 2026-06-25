package dev.d4vid.mods.genesis.server.pvp

import dev.d4vid.mods.genesis.server.config.GenesisConfig
import dev.d4vid.mods.genesis.server.event.GenesisCombatEvents

fun initializeCombatDamageMultiplier(config: GenesisConfig) {
    GenesisCombatEvents.MODIFY_MINECART_TNT_EXPLOSION_RADIUS.register {
        it * config.data.pvp.damageMultipliers.minecartTntExplosion
    }

    GenesisCombatEvents.MODIFY_RESPAWN_ANCHOR_EXPLOSION_RADIUS.register {
        it * config.data.pvp.damageMultipliers.respawnAnchorExplosion
    }

    GenesisCombatEvents.MODIFY_END_CRYSTAL_EXPLOSION_RADIUS.register {
        it * config.data.pvp.damageMultipliers.endCrystalExplosion
    }
}
