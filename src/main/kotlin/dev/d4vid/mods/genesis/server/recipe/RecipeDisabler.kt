package dev.d4vid.mods.genesis.server.recipe

import dev.d4vid.mods.genesis.server.GenesisConfig
import dev.d4vid.mods.genesis.server.event.RecipeAssembleCallback
import net.minecraft.world.InteractionResult

fun registerRecipeDisabler() {
    RecipeAssembleCallback.EVENT.register { input, result ->
        if (GenesisConfig.isRecipeDisabledForResult(result.item)) {
            return@register InteractionResult.FAIL
        }

        for (item in input) {
            if (GenesisConfig.isRecipeDisabledForInput(item.item)) {
                return@register InteractionResult.FAIL
            }
        }

        InteractionResult.PASS
    }
}
