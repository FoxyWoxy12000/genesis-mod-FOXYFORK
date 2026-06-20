package dev.d4vid.mods.genesis.server

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.d4vid.mods.genesis.server.item.CustomItems
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.permissions.Permissions

fun registerCommand() {
    CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
        dispatcher.register(command())
    }
}

private fun command(): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("genesis")
        .requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
        .then(reloadCommand())
        .then(giveCommand())
}

private fun reloadCommand(): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("reload")
        .requires { source -> !source.isPlayer }
        .executes { _ ->
            GenesisConfig.loadFile()

            0
        }
}

private fun giveCommand(): LiteralArgumentBuilder<CommandSourceStack> {
    val command = Commands.literal("give")

    CustomItems.REGISTERY.forEach { name, item ->
        command.then(
            Commands.literal(name)
                .executes { ctx ->
                    val player = ctx.source.playerOrException
                    player.inventory.add(item.create(player.level().registryAccess()))
                    0
                }
                .then(
                    Commands.argument("target", EntityArgument.player())
                        .executes { ctx ->
                            val target = EntityArgument.getPlayer(ctx, "target")
                            target.inventory.add(item.create(target.level().registryAccess()))
                            0
                        }
                )
        )
    }
    return command
}
