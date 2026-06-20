package dev.d4vid.mods.genesis.server

import dev.d4vid.mods.genesis.server.Genesis.logger
import dev.d4vid.mods.genesis.server.cooldown.CooldownType
import dev.d4vid.mods.genesis.server.serialization.DurationSecondsSerializer
import dev.d4vid.mods.genesis.server.serialization.EnumMapSerializer
import dev.d4vid.mods.genesis.server.serialization.GsonElementSerializer
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
    val combatOnly: Boolean = false,
)

@Serializable
private data class SpawnProtectionData(
    val enabled: Boolean = false,
    val x: Double = 0.0,
    val z: Double = 0.0,
    val radius: Double = 0.0,
)

@Serializable
private data class CombatDetectionData(
    val spawnProtection: SpawnProtectionData = SpawnProtectionData(),
    val minDamage: Double = 0.0,
    val damageScaling: Double = 1.0,
    val maxTimer: Double = 30.0,
    val combatLog: Boolean = false,
    val disableItems: Set<String> = setOf(),
)

@Serializable
private data class ResourcePackData(
    val url: String = "",
    val sha1: String = "",
    val prompt: String = "Please accept the resource pack.",
    val kickOnDecline: Boolean = false,
)

@Serializable
private data class DisableRecipesData(
    val using: Set<String> = setOf(),
    val withResult: Set<String> = setOf(),
)

@Serializable
data class ItemLimitData(
    val material: String,
    @Serializable(with = GsonElementSerializer::class)
    val nbt: com.google.gson.JsonElement? = null,
    val limit: Int,
)

@Serializable
data class ItemGroupLimitData(
    val material: String,
    @Serializable(with = GsonElementSerializer::class)
    val nbt: com.google.gson.JsonElement? = null,
    val scaling: Int,
)

@Serializable
data class ItemGroupData(
    val limit: Int,
    val items: List<ItemGroupLimitData>
)

@Serializable
private data class ItemLimitsData(
    val checkBundles: Boolean = false,
    val checkShulkers: Boolean = false,
    val limits: List<ItemLimitData> = listOf(),
    val groups: List<ItemGroupData> = listOf(),
)

@Serializable
private data class ConfigData(
    val disableNether: Boolean = false,
    val disableEnd: Boolean = false,
    val disableTotemDeathProtection: Boolean = false,
    val friendlyTeams: Set<String> = setOf(),
    @Serializable(with = CooldownsSerializer::class)
    val cooldowns: EnumMap<CooldownType, CooldownData> = EnumMap(CooldownType::class.java),
    val combatDetection: CombatDetectionData = CombatDetectionData(),
    val resourcePack: ResourcePackData = ResourcePackData(),
    val disableRecipes: DisableRecipesData = DisableRecipesData(),
    val itemLimits: ItemLimitsData = ItemLimitsData(),
)

object GenesisConfig {
    const val RESOURCE = "/config.json"
    const val PATH = "./config/genesis.json"

    private var data = ConfigData()

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

    fun isNetherDisabled() = data.disableEnd
    fun isEndDisabled() = data.disableNether

    fun isTotemDeathProtectionDisabled() = data.disableTotemDeathProtection

    fun isTeamFriendly(team: Team) = data.friendlyTeams.contains(team.name)

    fun getCooldownDuration(type: CooldownType): Duration = data.cooldowns[type]?.duration ?: Duration.ofMillis(0)
    fun isCooldownCombatOnly(type: CooldownType) = data.cooldowns[type]?.combatOnly ?: false

    fun isCombatSpawnProtectionEnabled() = data.combatDetection.spawnProtection.enabled
    fun getCombatSpawnProtectionX() = data.combatDetection.spawnProtection.x
    fun getCombatSpawnProtectionZ() = data.combatDetection.spawnProtection.z
    fun getCombatSpawnProtectionRadius() = data.combatDetection.spawnProtection.radius
    fun getCombatDetectionMinDamage() = data.combatDetection.minDamage
    fun getCombatDetectionDamageScaling() = data.combatDetection.damageScaling
    fun getCombatDetectionMaxTimer() = data.combatDetection.maxTimer
    fun isCombatLogEnabled() = data.combatDetection.combatLog
    fun isItemDisabledInCombat(item: Item) = data.combatDetection.disableItems.contains(getItemKey(item))

    fun getResourcePackUrl(): String = data.resourcePack.url
    fun getResourcePackSha1(): String = data.resourcePack.sha1
    fun getResourcePackPrompt(): String = data.resourcePack.prompt
    fun shouldKickOnResourcePackDecline(): Boolean = data.resourcePack.kickOnDecline

    fun isRecipeDisabledForInput(item: Item): Boolean = data.disableRecipes.using.contains(getItemKey(item))
    fun isRecipeDisabledForResult(item: Item): Boolean = data.disableRecipes.withResult.contains(getItemKey(item))

    fun shouldItemLimitsCheckBundles(): Boolean = data.itemLimits.checkBundles
    fun shouldItemLimitsCheckShulkers(): Boolean = data.itemLimits.checkShulkers
    fun getItemLimits(): List<ItemLimitData> = data.itemLimits.limits
    fun getItemLimitGroups(): List<ItemGroupData> = data.itemLimits.groups
}

private fun getItemKey(item: Item): String {
    return BuiltInRegistries.ITEM.getKey(item).toString()
}
