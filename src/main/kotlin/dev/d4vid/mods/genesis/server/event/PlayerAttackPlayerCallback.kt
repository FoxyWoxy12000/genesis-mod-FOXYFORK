package dev.d4vid.mods.genesis.server.event

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.entity.player.Player

fun interface PlayerAttackPlayerCallback {
    companion object {
        val EVENT = EventFactory.createArrayBacked(PlayerAttackPlayerCallback::class.java) { listeners ->
            PlayerAttackPlayerCallback { attacker, victim, damage ->
                for (listener in listeners) {
                    listener.interact(attacker, victim, damage)
                }
            }
        }
    }

    fun interact(attacker: Player, victim: Player, damage: Float)
}
