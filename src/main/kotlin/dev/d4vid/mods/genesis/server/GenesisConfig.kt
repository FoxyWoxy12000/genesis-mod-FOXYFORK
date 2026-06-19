package dev.d4vid.mods.genesis.server

import dev.d4vid.mods.genesis.server.Genesis.logger
import dev.d4vid.mods.genesis.server.cooldown.CooldownType
import dev.d4vid.mods.genesis.server.serialization.DurationSecondsSerializer
import dev.d4vid.mods.genesis.server.serialization.EnumMapSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.scores.Team
import java.io.File
import java.nio.file.Files
import java.time.Duration
import java.util.*

private object CooldownsSerializer : EnumMapSerializer<CooldownType, CooldownData>(
    CooldownType::class.java,
    CooldownType.serializer(),
    CooldownData.serializer()
)

@Serializable
private data class CooldownData(
    @Serializable(with = DurationSecondsSerializer::class)
    val duration: Duration,
    val combatOnly: Boolean,
)

@Serializable
private data class CombatDetectionData(
    val minDamage: Double,
    val damageScaling: Double,
    val maxTimer: Double,
    val combatLog: Boolean,
    val disableItems: Set<String>,
)

@Serializable
private data class ConfigData(
    val disableNether: Boolean,
    val disableEnd: Boolean,
    val friendlyTeams: Set<String>,
    @Serializable(with = CooldownsSerializer::class)
    val cooldowns: EnumMap<CooldownType, CooldownData>,
    val combatDetection: CombatDetectionData,
    @SerialName("ResourcePackStuff")
    val resourcePack: ResourcePackStuff,
)

@Serializable
private data class ResourcePackStuff(
    val Url: String,
    val Sha1: String,
    val KickOnDecline: Boolean,
    val Prompt: String
)

object GenesisConfig {
    const val RESOURCE = "/config.json"
    const val PATH = "./config/genesis.json"

    private var data = ConfigData(
        disableNether = false,
        disableEnd = false,
        friendlyTeams = setOf(),
        cooldowns = EnumMap(CooldownType::class.java),
        combatDetection = CombatDetectionData(
            minDamage = 1.0,
            damageScaling = 10.0,
            maxTimer = 30.0,
            combatLog = true,
            disableItems = setOf(),
        ),
        resourcePack = ResourcePackStuff(
            Url = "",
            Sha1 = "",
            KickOnDecline = true,
            Prompt = "This Server Requires A Resource Pack To Function, On Decline You WILL BE KICKED, also your choice will be remembered"
        ),
    )

    fun loadFile() {
        try {
            logger.info("Loading config...")

            var file = File(PATH)
            if (!file.exists()) {
                Files.createDirectories(file.parentFile.toPath())

                val stream = this.javaClass.getResourceAsStream(RESOURCE)!!
                Files.copy(stream, file.toPath())

                file = File(PATH)
            }

            data = Json.decodeFromString(ConfigData.serializer(), file.readText())

            logger.info("Successfully loaded config!")
        } catch (e: Exception) {
            logger.error("Failed to load config: ${e.stackTraceToString()}")
        }
    }

    fun isNetherDisabled(): Boolean {
        return data.disableEnd
    }

    fun isEndDisabled(): Boolean {
        return data.disableNether
    }

    fun isTeamFriendly(team: Team): Boolean {
        println(team.name)
        return data.friendlyTeams.contains(team.name)
    }

    fun getCooldownDuration(type: CooldownType): Duration {
        return data.cooldowns[type]?.duration ?: Duration.ofMillis(0)
    }

    fun isCooldownCombatOnly(type: CooldownType): Boolean {
        return data.cooldowns[type]?.combatOnly ?: false
    }

    fun getCombatDetectionMinDamage(): Double {
        return data.combatDetection.minDamage
    }

    fun getCombatDetectionDamageScaling(): Double {
        return data.combatDetection.damageScaling
    }

    fun getCombatDetectionMaxTimer(): Double {
        return data.combatDetection.maxTimer
    }

    fun isCombatLogEnabled(): Boolean {
        return data.combatDetection.combatLog
    }

    fun isItemDisabledInCombat(item: Item): Boolean {
        val id = BuiltInRegistries.ITEM.getKey(item).toString()
        println(id)
        return data.combatDetection.disableItems.contains(id)
    }

    fun getResourcePackUrl(): String = data.resourcePack.Url
    fun getResourcePackSha1(): String = data.resourcePack.Sha1
    fun isResourcePackKickOnDecline(): Boolean = data.resourcePack.KickOnDecline
    fun getResourcePackPrompt(): String = data.resourcePack.Prompt
}
