package dev.d4vid.mods.genesis.server.event

import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.server.level.ServerLevel

object GenesisPortalEvents {
    val ALLOW_NETHER = EventFactory.createArrayBacked(AllowNether::class.java) { listeners ->
        AllowNether { level ->
            for (listener in listeners) {
                val result = listener.allowNether(level)

                if (!result) {
                    return@AllowNether false
                }
            }

            true
        }
    }

    val ALLOW_END = EventFactory.createArrayBacked(AllowEnd::class.java) { listeners ->
        AllowEnd { level ->
            for (listener in listeners) {
                val result = listener.allowEnd(level)

                if (!result) {
                    return@AllowEnd false
                }
            }

            true
        }
    }

    fun interface AllowNether {
        fun allowNether(level: ServerLevel): Boolean
    }

    fun interface AllowEnd {
        fun allowEnd(level: ServerLevel): Boolean
    }
}
