package dev.d4vid.mods.genesis.server.cooldown

import dev.d4vid.mods.genesis.server.GenesisConfig
import dev.d4vid.mods.genesis.server.combat.isPlayerInCombat
import net.minecraft.server.level.ServerPlayer
import java.time.Duration
import java.time.Instant
import java.util.*

class CooldownManager(val type: CooldownType = CooldownType.Lunge) {
    private val cooldowns = mutableMapOf<UUID, Instant>()

    fun apply(player: ServerPlayer): Duration? {
        if (GenesisConfig.isCooldownCombatOnly(type) && !isPlayerInCombat(player)) {
            return Duration.ofSeconds(0)
        }

        val cooldown = cooldowns[player.uuid]
        val now = Instant.now()

        if (cooldown == null || cooldown.isBefore(now)) {
            val duration = GenesisConfig.getCooldownDuration(type)
            cooldowns[player.uuid] = now.plus(duration)

            return duration
        }

        return null
    }
}
