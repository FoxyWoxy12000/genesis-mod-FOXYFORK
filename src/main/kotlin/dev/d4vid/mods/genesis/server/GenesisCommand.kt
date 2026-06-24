package dev.d4vid.mods.genesis.server

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import dev.d4vid.mods.genesis.server.combat.PvpProtectionData
import dev.d4vid.mods.genesis.server.item.CustomItem
import dev.d4vid.mods.genesis.server.item.CustomItemSuggestionProvider
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
        .requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
        .executes { _ ->
            GenesisConfig.loadFile()

            Command.SINGLE_SUCCESS
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

                    Command.SINGLE_SUCCESS
                }
                .then(
                    Commands.argument("target", EntityArgument.player())
                        .executes { context ->
                            val player = EntityArgument.getPlayer(context, "target")
                            val item = getItemFromArg(context, "item")

                            player.inventory.add(item.create(player.level().registryAccess()))

                            Command.SINGLE_SUCCESS
                        }
                )
        )
}

private fun protectionCommand(): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("protection")
        .then(protectionEnableCommand())
        .then(protectionDisableCommand())
}

private fun protectionEnableCommand(): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("disable")
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

private fun protectionDisableCommand(): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("disable")
        .executes { context ->
            val player = context.source.playerOrException

            if (!PvpProtectionData.isProtected(player.uuid)) {
                ctx.source.sendFailure(Component.literal("You wern't protected to begin with gangalang."))
            } else  {
                PvpProtectionData.removeProtection(player.uuid)
                context.source.sendSuccess({ Component.literal("Your combat protection has been disabled.") }, false)
            }

            Command.SINGLE_SUCCESS
        }
        .then(
            Commands.argument("target", EntityArgument.player())
                .requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
                .executes { context ->
                    val target = EntityArgument.getPlayer(context, "target")

                    PvpProtectionData.grantProtection(target.uuid)
                    context.source.sendSuccess(
                        { Component.literal("Granted combat protection to ${target.name.string}.") },
                        false
                    )

                    Command.SINGLE_SUCCESS
                }
        )
}

private val UNKNOWN_CUSTOM_ITEM = SimpleCommandExceptionType(Component.literal("Unknown custom item."))

private fun getItemFromArg(context: CommandContext<*>, name: String): CustomItem {
    val itemName = StringArgumentType.getString(context, name)

    return CustomItems.REGISTERY[itemName] ?: throw UNKNOWN_CUSTOM_ITEM.create()
}
