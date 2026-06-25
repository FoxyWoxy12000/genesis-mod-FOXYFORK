package dev.d4vid.mods.genesis.server.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import dev.d4vid.mods.genesis.server.config.GenesisConfig
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permissions

private val LOAD_SUCCESS = Component.literal("Successfully reloaded the config")
private val LOAD_FAILED =
    SimpleCommandExceptionType(Component.literal("Failed to reload the config, see console for more info"))

fun reloadCommand(config: GenesisConfig): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("reload")
        .requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
        .executes { context ->
            if (!config.load()) {
                throw LOAD_FAILED.create()
            }

            context.source.sendSuccess({ LOAD_SUCCESS }, true)

            Command.SINGLE_SUCCESS
        }
}
