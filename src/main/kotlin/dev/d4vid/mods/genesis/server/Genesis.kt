package dev.d4vid.mods.genesis.server

import dev.d4vid.mods.genesis.server.combat.registerFriendlyTeamManager
import dev.d4vid.mods.genesis.server.combat.registerInCombatDetector
import net.fabricmc.api.DedicatedServerModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("unused")
object Genesis : DedicatedServerModInitializer {
    const val MOD_ID = "genesis"
    val logger: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitializeServer() {
        registerCommand()
        registerFriendlyTeamManager()
        registerInCombatDetector()

        GenesisConfig.loadFile()
    }
}
