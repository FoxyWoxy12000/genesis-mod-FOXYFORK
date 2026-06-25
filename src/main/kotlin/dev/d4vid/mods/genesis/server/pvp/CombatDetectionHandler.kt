package dev.d4vid.mods.genesis.server.pvp

import dev.d4vid.mods.genesis.server.config.GenesisConfig
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.projectile.arrow.AbstractArrow
import java.util.*
import kotlin.time.Clock
import kotlin.time.Instant

class CombatDetectionHandler(private val config: GenesisConfig) {
    companion object {
        private val OUT_OF_COMBAT = Component.literal("You are out of combat")
            .withStyle(ChatFormatting.GOLD)

        private fun inCombat(remaining: Long): Component {
            return Component.literal("In combat: ${remaining}s")
                .withStyle(ChatFormatting.RED)
        }
    }

    private val instants = mutableMapOf<UUID, Instant>()

    fun initialize() {
        CombatProtectionData.initialize()

        ServerLivingEntityEvents.AFTER_DAMAGE.register { victim, source, damage, _, _ ->
            if (victim !is ServerPlayer) {
                return@register
            }

            var attacker = source.entity
            if (attacker is AbstractArrow) {
                attacker = attacker.owner
            }
            if (attacker !is ServerPlayer || victim.uuid == attacker.uuid) {
                return@register
            }

            if (damage < config.data.pvp.detection.minDamage) {
                return@register
            }

            applyCombatTimer(victim, damage)
            applyCombatTimer(attacker, damage)
        }

        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            val player = handler.player

            if (!config.data.pvp.killPlayerOnCombatLog || !isPlayerInCombat(player)) {
                return@register
            }

            instants.remove(player.uuid)

            server.execute {
                player.kill(player.level())
            }
        }

        ServerLifecycleEvents.SERVER_STOPPING.register { _ ->
            instants.clear()
        }

        ServerTickEvents.END_SERVER_TICK.register { server ->
            if (server.tickCount % server.tickRateManager().tickrate().toInt() != 0) {
                return@register
            }

            val now = Clock.System.now()

            instants.entries.removeIf { (uuid, instant) ->
                val player = server.playerList.getPlayer(uuid)

                if (player == null || now > instant) {
                    player?.sendSystemMessage(OUT_OF_COMBAT, true)

                    true
                } else {
                    player.sendSystemMessage(inCombat((instant - now).inWholeSeconds), true)

                    false
                }
            }
        }
    }

    fun isPlayerInCombat(player: ServerPlayer): Boolean {
        return instants.containsKey(player.uuid)
    }

    private fun applyCombatTimer(player: ServerPlayer, damage: Float) {
        val now = Clock.System.now()
        val instant = instants[player.uuid] ?: now

        val scaled = config.data.pvp.detection.damageTimeSecondsScaling * damage.toDouble()
        val diff = maxOf(instant, now) - now
        val max = config.data.pvp.detection.maxTimeSeconds

        instants[player.uuid] = instant.plus(minOf(scaled, (max - diff)))
    }
}
