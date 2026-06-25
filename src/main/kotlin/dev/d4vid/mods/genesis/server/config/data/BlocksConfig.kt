package dev.d4vid.mods.genesis.server.config.data

import dev.d4vid.mods.genesis.server.config.field.NbtMatcher
import dev.d4vid.mods.genesis.server.config.serialization.BlockMatcher
import kotlinx.serialization.Serializable
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.state.BlockState

@Serializable
data class BlocksConfig(
    private val unbreakable: Set<BlockMatcher> = setOf(
        NbtMatcher(Identifier.withDefaultNamespace("spawner")),
    ),
) {
    fun isUnbreakable(state: BlockState): Boolean {
        return unbreakable.any {
            it.matchBlock(state)
        }
    }
}
