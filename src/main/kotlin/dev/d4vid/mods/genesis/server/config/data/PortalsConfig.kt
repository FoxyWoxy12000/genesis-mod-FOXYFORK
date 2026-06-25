package dev.d4vid.mods.genesis.server.config.data

import kotlinx.serialization.Serializable

@Serializable
data class PortalsConfig(
    val allowNether: Boolean = true,
    val allowEnd: Boolean = false,
)
