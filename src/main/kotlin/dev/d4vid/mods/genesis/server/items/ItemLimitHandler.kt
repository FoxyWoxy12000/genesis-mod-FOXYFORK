package dev.d4vid.mods.genesis.server.items

import dev.d4vid.mods.genesis.server.config.GenesisConfig
import dev.d4vid.mods.genesis.server.event.GenesisItemEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.ItemStack
import java.util.*

class ItemLimitHandler(private val config: GenesisConfig) {
    companion object {
        private val OVER_LIMIT_TITLE = Component.literal("Item limit reached").withStyle(ChatFormatting.RED)
        private val OVER_LIMIT = Component.literal("You feel sluggish...").withStyle(ChatFormatting.RED)
    }

    private val playersOverLimit = mutableSetOf<UUID>()

    private inline val itemsConfig
        get() = config.data.items

    fun initialize() {
        GenesisItemEvents.INVENTORY_ADD.register { player ->
            enforceItemLimits(player, true)
        }

        GenesisItemEvents.INVENTORY_CHANGE.register { player ->
            enforceItemLimits(player, false)
        }

        GenesisItemEvents.INVENTORY_CLOSE.register { player ->
            enforceItemLimits(player, true)
        }

        ServerPlayerEvents.JOIN.register { player ->
            enforceItemLimits(player, true)
        }

        ServerTickEvents.END_SERVER_TICK.register { server ->
            playersOverLimit.removeIf { uuid ->
                val player = server.playerList.getPlayer(uuid) ?: return@removeIf true

                for (effect in itemsConfig.overLimitEffects) {
                    BuiltInRegistries.MOB_EFFECT.get(effect.identifier).ifPresent {
                        player.addEffect(MobEffectInstance(it, 30, effect.strength, false, false, false))
                    }
                }

                false
            }
        }
    }

    private fun enforceItemLimits(player: ServerPlayer, inform: Boolean) {
        val limitCounts = itemsConfig.limits.associateWith { 0 }.toMutableMap()
        val groupLimitCounts = itemsConfig.groupLimits.associateWith { 0 }.toMutableMap()

        var overLimit = false

        scanItems(player.inventory.withIndex()) item@{ index, stack, isInv ->
            if (stack.isEmpty) {
                return@item
            }

            if (itemsConfig.shouldDiscardItem(stack)) {
                if (isInv) {
                    player.inventory.setItem(index, ItemStack.EMPTY)
                } else {
                    overLimit = true
                }

                return@item
            }

            itemsConfig.getLimitForItem(stack)?.let { limit ->
                limitCounts.merge(limit, stack.count, Int::plus)?.let {
                    if (it > limit.limit) {
                        overLimit = true
                    }
                }
            }

            itemsConfig.getGroupLimitForItem(stack)?.let { groupLimit ->
                val scaling = groupLimit.getScalingForItem(stack)
                if (scaling <= 0) {
                    return@let
                }

                groupLimitCounts.merge(groupLimit, stack.count * scaling, Int::plus)?.let {
                    if (it > groupLimit.limit) {
                        overLimit = true
                    }
                }
            }
        }

        if (!overLimit) {
            playersOverLimit.remove(player.uuid)
            return
        }

        playersOverLimit.add(player.uuid)

        if (inform) {
            player.connection.send(ClientboundSetTitleTextPacket(OVER_LIMIT_TITLE))
            player.connection.send(ClientboundSetSubtitleTextPacket(OVER_LIMIT))
            player.connection.send(ClientboundSetTitlesAnimationPacket(5, 40, 5))
        }
    }

    private fun scanItems(
        items: Iterable<IndexedValue<ItemStack>>,
        isInv: Boolean = true,
        itemHandler: (index: Int, stack: ItemStack, isInv: Boolean) -> Unit,
    ) {
        for ((index, stack) in items) {
            itemHandler(index, stack, isInv)

            if (itemsConfig.scanItemBundleContents) {
                stack.get(DataComponents.BUNDLE_CONTENTS)?.let {
                    scanItems(it.items().withIndex(), false, itemHandler)
                }
            }

            if (itemsConfig.scanItemContainers) {
                stack.get(DataComponents.CONTAINER)?.let {
                    scanItems(it.nonEmptyItems().withIndex(), false, itemHandler)
                }
            }
        }
    }
}
