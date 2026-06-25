package dev.d4vid.mods.genesis.server.command

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.d4vid.mods.genesis.server.custom.item.GenesisItems
import net.minecraft.commands.CommandSourceStack
import java.util.concurrent.CompletableFuture

class GenesisItemSuggestionProvider : SuggestionProvider<CommandSourceStack> {
    override fun getSuggestions(
        context: CommandContext<CommandSourceStack>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        for (itemName in GenesisItems.REGISTRY.keys) {
            builder.suggest(itemName.path)
        }

        return builder.buildFuture()
    }
}
