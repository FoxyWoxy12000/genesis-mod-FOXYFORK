package dev.d4vid.mods.genesis.server.cooldowns

import dev.d4vid.mods.genesis.server.config.GenesisConfig
import dev.d4vid.mods.genesis.server.pvp.CombatDetectionHandler
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.player.ItemEvents
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import java.util.*
import kotlin.time.Clock
import kotlin.time.Instant

class ItemCooldownHandler(private val config: GenesisConfig, private val combatDetection: CombatDetectionHandler) {
    private val cooldowns = mutableMapOf<UUID, MutableMap<Identifier, Instant>>()

    fun initialize() {
        ServerPlayerEvents.JOIN.register { player ->
            cooldowns[player.uuid] = mutableMapOf()
        }

        ServerPlayerEvents.LEAVE.register { player ->
            cooldowns.remove(player.uuid)
        }

        ItemEvents.USE.register { level, player, hand ->
            if (handle(level as ServerLevel, player as ServerPlayer, player.getItemBySlot(hand.asEquipmentSlot()))) {
                null
            } else {
                InteractionResult.FAIL
            }
        }

        ItemEvents.USE_ON.register { context ->
            if (handle(context.level as ServerLevel, context.player as ServerPlayer, context.itemInHand)) {
                null
            } else {
                InteractionResult.FAIL
            }
        }
    }

    private fun handle(level: ServerLevel, player: ServerPlayer, stack: ItemStack): Boolean {
        val cooldownConfig = config.data.cooldowns.getCooldownForItem(stack) ?: return true

        val identifier = cooldownConfig.match.identifier
        val playerCooldowns = cooldowns[player.uuid]!!

        if (playerCooldowns[identifier]?.let { it > Clock.System.now() } == true) {
            return false
        }

        val cooldown = if (combatDetection.isPlayerInCombat(player)) {
            cooldownConfig.inCombat
        } else {
            cooldownConfig.global
        }

        playerCooldowns[identifier] = Clock.System.now().plus(cooldown)

        val tickRate = level.server.tickRateManager().tickrate().toDouble()
        player.cooldowns.addCooldown(stack, (cooldown.inWholeSeconds * tickRate).toInt())

        return true
    }
}
