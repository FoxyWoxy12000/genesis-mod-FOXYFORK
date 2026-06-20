package dev.d4vid.mods.genesis.server.combat

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.server.level.ServerPlayer

fun registerPvpProtection() {
    ServerLivingEntityEvents.AFTER_DEATH.register { entity, source ->
        if (entity !is ServerPlayer) return@register
        val attacker = source.entity
        if (attacker !is ServerPlayer) return@register

        PvpProtectionData.grantProtection(entity.uuid)
    }
}
