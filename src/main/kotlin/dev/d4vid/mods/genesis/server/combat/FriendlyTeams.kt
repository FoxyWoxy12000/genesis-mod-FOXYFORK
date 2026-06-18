package dev.d4vid.mods.genesis.server.combat

import dev.d4vid.mods.genesis.server.GenesisConfig
import net.fabricmc.fabric.api.event.player.AttackEntityCallback
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player

fun registerFriendlyTeamManager() {
    AttackEntityCallback.EVENT.register { player, _, _, entity, _ ->
        if (entity !is Player) {
            return@register InteractionResult.PASS
        }

        val teams = arrayOf(player.team, entity.team)

        if (teams.any { it != null && GenesisConfig.isTeamFriendly(it) }) {
            InteractionResult.FAIL
        } else {
            InteractionResult.PASS
        }
    }
}
