package dev.d4vid.mods.genesis.server.config.data

import dev.d4vid.mods.genesis.server.config.data.cooldowns.CooldownsConfig
import dev.d4vid.mods.genesis.server.config.data.items.ItemsConfig
import dev.d4vid.mods.genesis.server.config.data.pvp.PvpConfig
import kotlinx.serialization.Serializable

@Serializable
data class ConfigData(
    val portals: PortalsConfig = PortalsConfig(),
    val blocks: BlocksConfig = BlocksConfig(),
    val recipes: RecipesConfig = RecipesConfig(),
    val cooldowns: CooldownsConfig = CooldownsConfig(),
    val pvp: PvpConfig = PvpConfig(),
    val items: ItemsConfig = ItemsConfig(),
)
