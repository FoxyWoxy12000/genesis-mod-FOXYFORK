package dev.d4vid.mods.genesis.server.recipes

import dev.d4vid.mods.genesis.server.config.GenesisConfig
import dev.d4vid.mods.genesis.server.event.GenesisRecipeEvents

fun initializeDisabledRecipeHandler(config: GenesisConfig) {
    GenesisRecipeEvents.ALLOW.register { input, result ->
        !config.data.recipes.isResultDisabled(result) && input.none {
            config.data.recipes.isIngredientDisabled(it)
        }
    }
}
