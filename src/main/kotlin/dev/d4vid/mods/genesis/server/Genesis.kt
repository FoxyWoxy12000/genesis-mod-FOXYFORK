package dev.d4vid.mods.genesis.server

import dev.d4vid.mods.genesis.server.combat.registerInCombatDetector
import dev.d4vid.mods.genesis.server.limit.registerInventoryLimitEnforcer
import dev.d4vid.mods.genesis.server.recipe.registerRecipeDisabler
import dev.d4vid.mods.genesis.server.resourcePack.ResourcePackPlayerData
import dev.d4vid.mods.genesis.server.resourcePack.registerResourcePackLoader
import net.fabricmc.api.DedicatedServerModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import dev.d4vid.mods.genesis.server.event.registerBloodlustKillTracker

@Suppress("unused")
object Genesis : DedicatedServerModInitializer {
    const val MOD_ID = "genesis"
    val logger: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitializeServer() {
        registerCommand()
        registerInCombatDetector()
        registerResourcePackLoader()
        registerRecipeDisabler()
        registerInventoryLimitEnforcer()

        GenesisConfig.loadFile()
        ResourcePackPlayerData.load()
        registerBloodlustKillTracker()
    }
}
