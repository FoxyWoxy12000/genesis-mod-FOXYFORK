package dev.d4vid.mods.genesis.server.limit

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import dev.d4vid.mods.genesis.server.GenesisConfig
import dev.d4vid.mods.genesis.server.ItemGroupData
import dev.d4vid.mods.genesis.server.ItemLimitData
import dev.d4vid.mods.genesis.server.event.PlayerInventoryInteractCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.*

private val overLimit = mutableSetOf<UUID>()

fun registerInventoryLimitEnforcer() {
    PlayerInventoryInteractCallback.EVENT.register { player ->
        discardItems(player)
        enforceInventoryLimits(player)
    }

    ServerPlayConnectionEvents.JOIN.register { listener, _, _ ->
        discardItems(listener.player)
        enforceInventoryLimits(listener.player)
    }

    ServerTickEvents.END_SERVER_TICK.register { server ->
        for (uuid in overLimit) {
            val player = server.playerList.getPlayer(uuid)

            player?.addEffect(MobEffectInstance(MobEffects.SLOWNESS, 20, 4))
            player?.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 30, 0))
        }
    }
}

private fun discardItems(player: ServerPlayer) {
    val inventory = player.inventory

    for (i in 0..<inventory.containerSize) {
        val stack = inventory.getItem(i).item

        if (GenesisConfig.shouldDiscardItem(stack)) {
            inventory.setItem(i, ItemStack.EMPTY)
        }
    }
}

private fun enforceInventoryLimits(player: ServerPlayer) {
    val checkBundles = GenesisConfig.shouldItemLimitsCheckBundles()
    val checkShulkers = GenesisConfig.shouldItemLimitsCheckShulkers()

    val limits = GenesisConfig.getItemLimits()
    val groups = GenesisConfig.getItemLimitGroups()

    val limitCounts = mutableMapOf<ItemLimitData, Int>()
    val groupCounts = mutableMapOf<ItemGroupData, Int>()

    val ok = recurse(Iterable { player.inventory.iterator() }, checkBundles, checkShulkers) { stack ->
        if (stack.isEmpty) {
            return@recurse true
        }

        for (limit in limits) {
            if (!matchesLimit(limit, stack)) {
                continue
            }

            val count = (limitCounts[limit] ?: 0) + stack.count
            limitCounts[limit] = count

            if (count > limit.limit) {
                return@recurse false
            }
        }

        for (group in groups) {
            val scaling = groupScaling(group, stack)
            if (scaling <= 0) {
                continue
            }

            val count = (groupCounts[group] ?: 0) + stack.count * scaling
            groupCounts[group] = count

            if (count > group.limit) {
                return@recurse false
            }
        }

        return@recurse true
    }

    if (ok) {
        overLimit.remove(player.uuid)
        return
    }

    overLimit.add(player.uuid)
    player.sendSystemMessage(Component.literal("You feel sluggish...").withStyle(ChatFormatting.RED), true)
}

private fun recurse(
    items: Iterable<ItemStack>,
    checkBundles: Boolean,
    checkShulkers: Boolean,
    func: (stack: ItemStack) -> Boolean
): Boolean {
    for (stack in items) {
        if (!func(stack)) {
            return false
        }

        if (stack.item == Items.BUNDLE && checkBundles) {
            stack.get(DataComponents.BUNDLE_CONTENTS)?.let {
                if (!recurse(it.items(), checkBundles, checkShulkers, func)) {
                    return false
                }
            }
        } else if (stack.item == Items.SHULKER_BOX && checkShulkers) {
            stack.get(DataComponents.CONTAINER)?.let {
                if (!recurse(it.nonEmptyItems(), checkBundles, checkShulkers, func)) {
                    return false
                }
            }
        }
    }

    return true
}

private fun matchesLimit(limit: ItemLimitData, stack: ItemStack): Boolean {
    if (!matchesMaterial(limit.material, stack)) {
        return false
    }

    return limit.nbt == null || doesNbtMatch(limit.nbt, stack)
}

private fun groupScaling(group: ItemGroupData, stack: ItemStack): Int {
    for (item in group.items) {
        if (!matchesMaterial(item.material, stack)) {
            continue
        }

        if (item.nbt == null || doesNbtMatch(item.nbt, stack)) {
            return item.scaling
        }
    }

    return 0
}

private fun matchesMaterial(material: String, stack: ItemStack): Boolean {
    return material == BuiltInRegistries.ITEM.getKey(stack.item).toString()
}

private fun doesNbtMatch(json: JsonElement, stack: ItemStack): Boolean {
    val nbtJson = DataComponentMap.CODEC.encodeStart(
        JsonOps.INSTANCE,
        stack.components
    ).getOrThrow()

    return doesJsonMatch(json, nbtJson)
}

private fun doesJsonMatch(a: JsonElement, b: JsonElement): Boolean {
    if (a.isJsonObject) {
        if (!b.isJsonObject) return false

        val aObj = a.getAsJsonObject()
        val bObj = b.getAsJsonObject()

        for (entry in aObj.entrySet()) {
            val key = entry.key

            if (!bObj.has(key) || !doesJsonMatch(entry.value, bObj.get(key))) {
                return false
            }
        }

        return true
    }

    if (a.isJsonArray) {
        if (!b.isJsonArray) return false

        val aArray = a.getAsJsonArray()
        val bArray = b.getAsJsonArray()

        if (aArray.size() != bArray.size()) {
            return false
        }

        for (i in 0..<aArray.size()) {
            if (!doesJsonMatch(aArray.get(i), bArray.get(i))) {
                return false
            }
        }

        return true
    }

    return a == b
}
