package dev.d4vid.mods.genesis.server.cooldowns

import dev.d4vid.mods.genesis.server.config.GenesisConfig
import dev.d4vid.mods.genesis.server.event.GenesisCooldownEvents
import dev.d4vid.mods.genesis.server.pvp.CombatDetectionHandler
import net.minecraft.tags.ItemTags
import java.util.*
import kotlin.time.Clock
import kotlin.time.Instant

class LungeCooldownHandler(private val config: GenesisConfig, private val combatDetection: CombatDetectionHandler) {
    private val cooldowns = mutableMapOf<UUID, Instant>()

    fun initialize() {
        GenesisCooldownEvents.ALLOW_LUNGE.register { level, _, itemInUse, player, _ ->
            val stack = itemInUse.itemStack

            if (!stack.`is`(ItemTags.SPEARS)) {
                return@register true
            }

            if (cooldowns[player.uuid]?.let { it > Clock.System.now() } == true) {
                return@register false
            }

            val cooldown = if (combatDetection.isPlayerInCombat(player)) {
                config.data.cooldowns.lunge.inCombat
            } else {
                config.data.cooldowns.lunge.global
            }

            cooldowns[player.uuid] = Clock.System.now().plus(cooldown)

            val tickRate = level.server.tickRateManager().tickrate()
            player.cooldowns.addCooldown(stack, (cooldown.inWholeSeconds * tickRate).toInt())

            true
        }

        GenesisCooldownEvents.ALLOW_ITEM_COOLDOWN.register { stack ->
            !stack.`is`(ItemTags.SPEARS)
        }
    }
}
