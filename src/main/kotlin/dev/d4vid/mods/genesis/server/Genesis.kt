package dev.d4vid.mods.genesis.server

import dev.d4vid.mods.genesis.server.combat.registerFriendlyTeamManager
import dev.d4vid.mods.genesis.server.combat.registerInCombatDetector
import net.fabricmc.api.DedicatedServerModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import dev.d4vid.mods.genesis.server.mixin.resourcePack.ResourcePackPlayerData

@Suppress("unused")
object Genesis : DedicatedServerModInitializer {
    const val MOD_ID = "genesis"
    val logger: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitializeServer() {
        registerCommand()
        registerFriendlyTeamManager()
        registerInCombatDetector()

        GenesisConfig.loadFile()
        registerResourcePackLoader()
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            ResourcePackPlayerData.load(server)
        }
    }
}
