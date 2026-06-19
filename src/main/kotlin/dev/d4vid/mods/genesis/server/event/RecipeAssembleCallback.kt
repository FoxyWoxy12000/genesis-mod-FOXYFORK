package dev.d4vid.mods.genesis.server.event

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput

fun interface RecipeAssembleCallback {
    companion object {
        val EVENT = EventFactory.createArrayBacked(RecipeAssembleCallback::class.java) { listeners ->
            RecipeAssembleCallback { input, result ->
                for (listener in listeners) {
                    val result = listener.interact(input, result)

                    if (result != InteractionResult.PASS) {
                        return@RecipeAssembleCallback result
                    }
                }

                InteractionResult.PASS
            }
        }
    }

    fun interact(
        input: RecipeInput,
        result: ItemStack
    ): InteractionResult
}
