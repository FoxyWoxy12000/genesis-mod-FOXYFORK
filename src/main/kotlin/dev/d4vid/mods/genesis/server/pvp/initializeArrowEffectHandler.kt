package dev.d4vid.mods.genesis.server.pvp

import dev.d4vid.mods.genesis.server.config.GenesisConfig
import dev.d4vid.mods.genesis.server.event.GenesisCombatEvents

fun initializeArrowEffectHandler(config: GenesisConfig) {
    GenesisCombatEvents.ALLOW_ARROW_EFFECT.register {
        !config.data.pvp.isArrowEffectDisabled(it)
    }
}
