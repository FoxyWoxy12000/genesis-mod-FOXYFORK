package dev.d4vid.mods.genesis.server.pvp

import dev.d4vid.mods.genesis.server.config.GenesisConfig
import dev.d4vid.mods.genesis.server.event.GenesisCombatEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.projectile.arrow.AbstractArrow
import java.util.*
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class CombatProtectionHandler(private val config: GenesisConfig) {
    companion object {
        private val VICTIM_SPAWN_PROTECTED =
            Component.literal("This player is spawn protected").withStyle(ChatFormatting.RED)
        private val ATTACKER_SPAWN_PROTECTED =
            Component.literal("You are spawn protected").withStyle(ChatFormatting.RED)
        private val COMBAT_PROTECTION_EXPIRED =
            Component.literal("You are no longer combat protected").withStyle(ChatFormatting.RED)

        private fun combatProtected(remaining: Long): Component {
            val minutes = remaining / 60
            val seconds = remaining % 60

            return Component.literal("Combat protection: ${minutes}m ${seconds}s")
                .withStyle(ChatFormatting.GREEN)
        }

        private fun victimCombatProtected(remaining: Long): Component {
            val minutes = remaining / 60
            val seconds = remaining % 60

            return Component.literal("This player is combat protected for ${minutes}m ${seconds}s")
                .withStyle(ChatFormatting.RED)
        }
    }

    private val data = mutableMapOf<UUID, CombatProtectionData>()

    fun initialize() {
        GenesisCombatEvents.ALLOW_PET_DAMAGE.register { _, pet, source, _ ->
            if (!config.data.pvp.protectHarmlessPets) {
                return@register true
            }

            var attacker = source.entity
            if (attacker is AbstractArrow) {
                attacker = attacker.owner
            }
            if (attacker !is ServerPlayer) {
                return@register true
            }

            return@register attacker == pet.owner || pet.target is ServerPlayer
        }

        ServerPlayerEvents.JOIN.register { player ->
            data[player.uuid] = CombatProtectionData(
                player,
                config.data.pvp.joinProtectionMinutes,
            )
        }

        ServerPlayerEvents.AFTER_RESPAWN.register { _, player, _ ->
            data[player.uuid] = CombatProtectionData(
                player,
                config.data.pvp.respawnProtectionMinutes,
            )
        }

        ServerLivingEntityEvents.ALLOW_DAMAGE.register { victim, source, _ ->
            if (victim !is ServerPlayer) {
                return@register true
            }

            var attacker = source.entity
            if (attacker is AbstractArrow) {
                attacker = attacker.owner
            }
            if (attacker !is ServerPlayer || attacker.uuid == victim.uuid) {
                return@register true
            }

            val teams = arrayOf(victim.team, attacker.team)
            if (teams.any { it != null && config.data.pvp.isTeamProtected(it) }) {
                return@register false
            }

            data[victim.uuid]?.let {
                attacker.sendSystemMessage(victimCombatProtected(it.getRemainingSeconds()), true)

                return@register false
            }

            if (data.containsKey(attacker.uuid)) {
                return@register false
            }

            if (config.data.pvp.isSpawnProtected(victim)) {
                attacker.sendSystemMessage(VICTIM_SPAWN_PROTECTED, true)

                return@register false
            }

            if (config.data.pvp.isSpawnProtected(attacker)) {
                attacker.sendSystemMessage(ATTACKER_SPAWN_PROTECTED, true)

                return@register false
            }

            true
        }

        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
            data.remove(handler.player.uuid)?.saveRemainingSeconds()
        }

        ServerLifecycleEvents.SERVER_STOPPING.register { server ->
            for (player in server.playerList.players) {
                data.remove(player.uuid)?.saveRemainingSeconds()
            }
        }

        ServerTickEvents.END_SERVER_TICK.register { server ->
            if (server.tickCount % server.tickRateManager().tickrate().toInt() != 0) {
                return@register
            }

            val now = Clock.System.now()

            data.entries.removeIf { (uuid, playerData) ->
                val player = server.playerList.getPlayer(uuid)
                val remaining = playerData.saveRemainingSeconds()

                if (player == null || now > playerData.instant) {
                    player?.sendSystemMessage(COMBAT_PROTECTION_EXPIRED, true)

                    true
                } else {
                    player.sendSystemMessage(combatProtected(remaining), true)

                    false
                }
            }
        }
    }

    fun set(player: ServerPlayer, duration: Duration) {
        data[player.uuid] = CombatProtectionData(player, duration, true)
    }

    fun remove(player: ServerPlayer) {
        data[player.uuid] = CombatProtectionData(player, 0.seconds, true)
    }
}
