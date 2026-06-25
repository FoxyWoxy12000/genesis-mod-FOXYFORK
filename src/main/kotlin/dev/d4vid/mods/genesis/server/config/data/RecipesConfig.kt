package dev.d4vid.mods.genesis.server.config.data

import dev.d4vid.mods.genesis.server.config.field.NbtMatcher
import dev.d4vid.mods.genesis.server.config.serialization.ItemMatcher
import kotlinx.serialization.Serializable
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack

@Serializable
data class RecipesConfig(
    private val disableIngredients: Set<ItemMatcher> = setOf(
        NbtMatcher(Identifier.withDefaultNamespace("netherite_upgrade_smithing_template")),
    ),
    private val disableResults: Set<ItemMatcher> = setOf(
        NbtMatcher(Identifier.withDefaultNamespace("mace")),
    ),
) {
    fun isIngredientDisabled(stack: ItemStack): Boolean {
        return disableIngredients.any {
            it.matchItem(stack)
        }
    }

    fun isResultDisabled(stack: ItemStack): Boolean {
        return disableResults.any {
            it.matchItem(stack)
        }
    }
}
