package dev.d4vid.mods.genesis.server.items

import dev.d4vid.mods.genesis.server.config.GenesisConfig
import dev.d4vid.mods.genesis.server.event.GenesisItemEvents
import dev.d4vid.mods.genesis.server.pvp.CombatDetectionHandler
import net.fabricmc.fabric.api.event.player.ItemEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack

fun initializeDisabledItemsHandler(config: GenesisConfig, combatDetection: CombatDetectionHandler) {
    GenesisItemEvents.ALLOW_TOTEM.register { _ ->
        !config.data.items.disableTotemDeathProtection
    }

    ItemEvents.USE.register { _, player, hand ->
        val stack = player.getItemBySlot(hand.asEquipmentSlot())

        if (allowItemUsage(config, combatDetection, player as ServerPlayer, stack)) {
            null
        } else {
            InteractionResult.FAIL
        }
    }

    ItemEvents.USE_ON.register { context ->
        if (allowItemUsage(config, combatDetection, context.player as ServerPlayer, context.itemInHand)) {
            null
        } else {
            InteractionResult.FAIL
        }
    }
}

private fun allowItemUsage(
    config: GenesisConfig,
    combatDetection: CombatDetectionHandler,
    player: ServerPlayer,
    stack: ItemStack
): Boolean {
    return !config.data.items.isItemUsageDisabled(stack, combatDetection.isPlayerInCombat(player))
}
