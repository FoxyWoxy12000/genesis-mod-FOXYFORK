package dev.d4vid.mods.genesis.server.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.d4vid.mods.genesis.server.config.GenesisConfig
import dev.d4vid.mods.genesis.server.pvp.CombatProtectionHandler
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.permissions.Permissions
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

private fun protectionSet(target: String, duration: Long): Component {
    val minutes = duration / 60
    val seconds = duration % 60

    return Component.literal("Set $target combat protection for ${minutes}m ${seconds}s")
}

private fun protectionRemoved(target: String): Component {
    return Component.literal("Removed $target combat protection")
}

fun protectionCommand(
    config: GenesisConfig,
    combatProtection: CombatProtectionHandler
): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("protection")
        .then(setCommand(config, combatProtection))
        .then(removeCommand(combatProtection))
}

private fun setCommand(
    config: GenesisConfig,
    combatProtection: CombatProtectionHandler
): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("set")
        .requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
        .then(
            Commands.argument("target", EntityArgument.player())
                .executes { context ->
                    val target = EntityArgument.getPlayer(context, "target")
                    val duration = config.data.pvp.respawnProtectionMinutes

                    setProtection(combatProtection, context, target, duration)

                    Command.SINGLE_SUCCESS
                }
                .then(
                    Commands.argument("duration", IntegerArgumentType.integer())
                        .executes { context ->
                            val target = EntityArgument.getPlayer(context, "target")
                            val duration = IntegerArgumentType.getInteger(context, "duration")

                            setProtection(combatProtection, context, target, duration.minutes)

                            Command.SINGLE_SUCCESS
                        }
                )
        )
}

private fun removeCommand(
    combatProtection: CombatProtectionHandler
): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("remove")
        .executes { context ->
            removeProtection(combatProtection, context, context.source.playerOrException)

            Command.SINGLE_SUCCESS
        }
        .then(
            Commands.argument("target", EntityArgument.player())
                .requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
                .executes { context ->
                    val target = EntityArgument.getPlayer(context, "target")

                    removeProtection(combatProtection, context, target)

                    Command.SINGLE_SUCCESS
                }
        )
}

private fun setProtection(
    combatProtection: CombatProtectionHandler,
    context: CommandContext<CommandSourceStack>,
    target: ServerPlayer,
    duration: Duration,
) {
    combatProtection.set(target, duration)

    context.source.sendSuccess(
        { protectionSet(targetName(context, target), duration.inWholeSeconds) },
        true,
    )
}

private fun removeProtection(
    combatProtection: CombatProtectionHandler,
    context: CommandContext<CommandSourceStack>,
    target: ServerPlayer,
) {
    combatProtection.remove(target)

    context.source.sendSuccess(
        { protectionRemoved(targetName(context, target)) },
        context.source != target,
    )
}

private fun targetName(
    context: CommandContext<CommandSourceStack>,
    target: ServerPlayer,
) = if (context.source.player?.uuid == target.uuid) "own" else "${target.gameProfile.name}'s"
