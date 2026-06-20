package dev.d4vid.mods.genesis.server.event

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import dev.d4vid.mods.genesis.server.item.items.Bloodlust

fun registerBloodlustKillTracker() {
    ServerLivingEntityEvents.AFTER_DEATH.register { entity, source ->
        if (entity !is ServerPlayer) return@register
        val killed = entity

        val attacker = source.entity
        if (attacker !is ServerPlayer) return@register
        val stack = attacker.mainHandItem
        val model = stack.get(DataComponents.ITEM_MODEL)
        if (model != Identifier.tryParse("genesis:bloodlust")) return@register

        Bloodlust.onKill(stack, attacker, killed, attacker.level().registryAccess())
    }
}
