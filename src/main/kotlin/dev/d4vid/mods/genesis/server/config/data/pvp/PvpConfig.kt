package dev.d4vid.mods.genesis.server.config.data.pvp

import dev.d4vid.mods.genesis.server.config.serialization.IdentifierSerializer
import dev.d4vid.mods.genesis.server.config.serialization.NonNegativeDurationMinutesDoubleSerializer
import kotlinx.serialization.Serializable
import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.scores.Team
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
data class PvpConfig(
    val protectHarmlessPets: Boolean = true,
    private val protectedTeams: Set<String> = setOf("peaceful"),
    private val spawnProtection: PvpSpawnProtectionConfig = PvpSpawnProtectionConfig(),
    @Serializable(with = NonNegativeDurationMinutesDoubleSerializer::class)
    val joinProtectionMinutes: Duration = 5.minutes,
    @Serializable(with = NonNegativeDurationMinutesDoubleSerializer::class)
    val respawnProtectionMinutes: Duration = 60.minutes,
    val detection: PvpDetectionConfig = PvpDetectionConfig(),
    val killPlayerOnCombatLog: Boolean = true,
    val damageMultipliers: PvpDamageMultipliersConfig = PvpDamageMultipliersConfig(),
    private val disableTippedArrows: List<@Serializable(with = IdentifierSerializer::class) Identifier> = listOf(
        Identifier.withDefaultNamespace("weakness"),
        Identifier.withDefaultNamespace("long_weakness"),
        Identifier.withDefaultNamespace("slow_falling"),
        Identifier.withDefaultNamespace("long_slow_falling"),
        Identifier.withDefaultNamespace("turtle_master"),
        Identifier.withDefaultNamespace("long_turtle_master"),
        Identifier.withDefaultNamespace("strong_turtle_master"),
    ),
) {
    fun isTeamProtected(team: Team): Boolean {
        return protectedTeams.contains(team.name)
    }

    fun isSpawnProtected(player: ServerPlayer): Boolean {
        val dx = player.x - spawnProtection.x
        val dz = player.z - spawnProtection.z
        val radius = spawnProtection.radius

        return (dx * dx + dz * dz) <= (radius * radius)
    }

    fun isArrowPotionDisabled(potion: Holder<Potion>): Boolean {
        return disableTippedArrows.any { potion.`is`(it) }
    }
}
