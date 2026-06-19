package dev.d4vid.mods.genesis.server.recipe

import dev.d4vid.mods.genesis.server.GenesisConfig
import dev.d4vid.mods.genesis.server.event.RecipeAssembleCallback
import net.minecraft.world.InteractionResult

fun registerRecipeDisabler() {
    RecipeAssembleCallback.EVENT.register { input, result ->
        if (GenesisConfig.isRecipeDisabledForResult(result.item)) {
            return@register InteractionResult.FAIL
        }

        for (i in 0..<input.size()) {
            val item = input.getItem(i).item

            if (GenesisConfig.isRecipeDisabledForInput(item)) {
                return@register InteractionResult.FAIL
            }
        }

        InteractionResult.PASS
    }
}
