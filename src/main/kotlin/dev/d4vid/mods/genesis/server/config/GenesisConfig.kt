package dev.d4vid.mods.genesis.server.config

import dev.d4vid.mods.genesis.server.config.data.ConfigData
import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class GenesisConfig {
    companion object {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = true
            prettyPrintIndent = "  "
        }
    }

    private val logger: Logger = LoggerFactory.getLogger(GenesisConfig::class.java)

    var path: Path = Path.of("config", "genesis.json")
    var data = ConfigData()
        private set

    fun initialize() {
        ServerLifecycleEvents.SERVER_STARTING.register { server ->
            path = server.serverDirectory / "config" / "genesis.json"
            load()
        }
    }

    fun load(): Boolean {
        try {
            logger.info("Loading config...")

            if (path.exists()) {
                data = json.decodeFromString(ConfigData.serializer(), path.readText())
            }

            Files.createDirectories(path.parent)
            path.writeText(json.encodeToString(data))

            logger.info("Successfully loaded config!")

            return true
        } catch (e: Exception) {
            logger.error("Error when loading config: ${e.stackTraceToString()}")

            return false
        }
    }
}
