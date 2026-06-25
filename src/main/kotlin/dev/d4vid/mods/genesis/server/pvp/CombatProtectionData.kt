package dev.d4vid.mods.genesis.server.pvp

import com.mojang.serialization.Codec
import dev.d4vid.mods.genesis.server.Genesis
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Suppress("UnstableApiUsage")
class CombatProtectionData(
    private val target: ServerPlayer,
    duration: Duration? = null,
    force: Boolean = false,
) {
    companion object {
        private var PROTECTION_DISABLE = "/genesis protection remove"
        private var COMBAT_PROTECTION = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Genesis.MOD_ID, "combat_protection"),
        ) { builder ->
            builder.persistent(Codec.LONG).copyOnDeath()
        }

        fun initialize() {}

        private fun combatProtected(minutes: Long): Component {
            return Component.literal("Combat protection enabled for ${minutes}m\nTo disable, run ")
                .append(
                    Component.literal(PROTECTION_DISABLE)
                        .withStyle { it.withClickEvent(ClickEvent.SuggestCommand(PROTECTION_DISABLE)) })
                .withStyle(ChatFormatting.GREEN)
        }
    }

    val instant: Instant

    init {
        val read = target.getAttachedOrElse(COMBAT_PROTECTION, 0).seconds
        val now = Clock.System.now()

        instant = now.plus(duration?.takeIf { force || it > read } ?: read)

        if (instant > now) {
            target.sendSystemMessage(combatProtected((instant - now).inWholeMinutes))
        }
    }

    fun getRemainingSeconds(): Long {
        return (instant - Clock.System.now()).inWholeSeconds
    }

    fun saveRemainingSeconds(): Long {
        val remaining = max(getRemainingSeconds(), 0)

        if (remaining == 0L) {
            target.removeAttached(COMBAT_PROTECTION)
        } else {
            target.setAttached(COMBAT_PROTECTION, remaining)
        }

        return remaining
    }
}
