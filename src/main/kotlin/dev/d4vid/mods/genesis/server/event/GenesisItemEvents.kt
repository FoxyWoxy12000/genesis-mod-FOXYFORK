package dev.d4vid.mods.genesis.server.event

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource

object GenesisItemEvents {
    val ALLOW_TOTEM = EventFactory.createArrayBacked(AllowTotem::class.java) { listeners ->
        AllowTotem { source ->
            for (listener in listeners) {
                val result = listener.allowTotem(source)

                if (!result) {
                    return@AllowTotem false
                }
            }

            true
        }
    }

    val INVENTORY_ADD = EventFactory.createArrayBacked(InventoryAdd::class.java) { listeners ->
        InventoryAdd { player ->
            for (listener in listeners) {
                listener.inventoryAdd(player)
            }
        }
    }

    val INVENTORY_CHANGE = EventFactory.createArrayBacked(InventoryChange::class.java) { listeners ->
        InventoryChange { player ->
            for (listener in listeners) {
                listener.inventoryChange(player)
            }
        }
    }

    val INVENTORY_CLOSE = EventFactory.createArrayBacked(InventoryClose::class.java) { listeners ->
        InventoryClose { player ->
            for (listener in listeners) {
                listener.inventoryClose(player)
            }
        }
    }

    fun interface AllowTotem {
        fun allowTotem(source: DamageSource): Boolean
    }

    fun interface InventoryAdd {
        fun inventoryAdd(player: ServerPlayer)
    }

    fun interface InventoryChange {
        fun inventoryChange(player: ServerPlayer)
    }

    fun interface InventoryClose {
        fun inventoryClose(player: ServerPlayer)
    }
}
