package dev.d4vid.mods.genesis.server.combat

import dev.d4vid.mods.genesis.server.GenesisConfig
import dev.d4vid.mods.genesis.server.event.PlayerAttackPlayerCallback
import dev.d4vid.mods.genesis.server.event.PlayerItemUseCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.math.min

private const val SECOND_MILLIS = 1000
private val timers = mutableMapOf<UUID, Instant>()

fun isPlayerInCombat(player: Player): Boolean {
    return timers[player.uuid]?.isAfter(Instant.now()) ?: false
}

private val outOfCombatComponent = Component.literal("You are out of combat!").withStyle(ChatFormatting.GOLD)
private fun inCombatComponent(remaining: Long): Component {
    return Component.literal("In combat for $remaining seconds").withStyle(ChatFormatting.RED)
}

fun registerInCombatDetector() {
    PlayerAttackPlayerCallback.EVENT.register { attacker, victim, damage ->
        if (damage < GenesisConfig.getCombatDetectionMinDamage()) {
            return@register
        }

        applyCombatTimer(attacker, damage)
        applyCombatTimer(victim, damage)
    }

    PlayerItemUseCallback.EVENT.register { player, _, stack, _, _ ->
        if (isPlayerInCombat(player) && GenesisConfig.isItemDisabledInCombat(stack.item)) {
            InteractionResult.FAIL
        } else {
            InteractionResult.PASS
        }
    }

    ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
        val player = handler.player

        if (!isPlayerInCombat(player) || !GenesisConfig.isCombatLogEnabled()) {
            return@register
        }

        server.execute {
            player.kill(player.level())
        }
    }

    ServerTickEvents.END_SERVER_TICK.register { server ->
        val now = Instant.now()

        timers.entries.removeIf { (uuid, timer) ->
            val player = server.playerList.getPlayer(uuid)

            if (player == null || now.isAfter(timer)) {
                player?.sendSystemMessage(outOfCombatComponent, true)

                true
            } else {
                player.sendSystemMessage(inCombatComponent(Duration.between(now, timer).seconds), true)

                false
            }
        }
    }
}

private fun applyCombatTimer(player: Player, damage: Float) {
    val now = Instant.now()
    val timer = timers[player.uuid] ?: now

    val scaled = damage * GenesisConfig.getCombatDetectionDamageScaling()
    val diff = Duration.between(now, timer).seconds
    val max = GenesisConfig.getCombatDetectionMaxTimer()

    val seconds = min(scaled * SECOND_MILLIS, (max - diff) * SECOND_MILLIS)

    timers[player.uuid] = timer.plusMillis(seconds.toLong())
}
