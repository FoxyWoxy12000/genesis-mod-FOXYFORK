package dev.d4vid.mods.genesis.server

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.d4vid.mods.genesis.server.combat.PvpProtectionData
import dev.d4vid.mods.genesis.server.item.CustomItems
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permissions

fun registerCommand() {
    CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
        dispatcher.register(command())
    }
}

private fun command(): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("genesis")
        //.requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
        .then(reloadCommand())
        .then(giveCommand())
        .then(protectionCommand())
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
        .requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
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

private fun protectionCommand(): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("protection")
        .then(disableOwnProtectionCommand())
        .then(adminProtectionCommand())
}

private fun adminProtectionCommand(): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("admin")
        .requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
        .then(Commands.literal("on")
            .then(Commands.argument("target", EntityArgument.player())
                .executes { ctx ->
                    val target = EntityArgument.getPlayer(ctx, "target")
                    PvpProtectionData.grantProtection(target.uuid)
                    ctx.source.sendSuccess({ Component.literal("Granted protection to ${target.name.string}.") }, false)
                    0
                }
            )
        )
        .then(Commands.literal("off")
            .then(Commands.argument("target", EntityArgument.player())
                .executes { ctx ->
                    val target = EntityArgument.getPlayer(ctx, "target")
                    PvpProtectionData.removeProtection(target.uuid)
                    ctx.source.sendSuccess({ Component.literal("Removed protection from ${target.name.string}.") }, false)
                    0
                }
            )
        )
}

private fun disableOwnProtectionCommand(): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("disable")
        .requires { source -> source.isPlayer }
        .executes { ctx ->
            val player = ctx.source.playerOrException
            if (!PvpProtectionData.isProtected(player.uuid)) {
                ctx.source.sendFailure(Component.literal("You wern't protected to begin with gangalang."))
            } else  {
                PvpProtectionData.removeProtection(player.uuid)
                ctx.source.sendSuccess({ Component.literal("Your protection has been disabled.") }, false)
            }
            0
        }
}
