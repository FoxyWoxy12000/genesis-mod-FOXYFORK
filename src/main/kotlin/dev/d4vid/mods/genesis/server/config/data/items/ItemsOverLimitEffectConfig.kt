package dev.d4vid.mods.genesis.server.config.data.items

import dev.d4vid.mods.genesis.server.config.serialization.IdentifierSerializer
import kotlinx.serialization.Serializable
import net.minecraft.resources.Identifier

@Serializable
data class ItemsOverLimitEffectConfig(
    @Serializable(with = IdentifierSerializer::class)
    val identifier: Identifier,
    val strength: Int = 0,
)
