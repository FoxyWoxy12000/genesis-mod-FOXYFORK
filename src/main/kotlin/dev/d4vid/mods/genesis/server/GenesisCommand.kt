package dev.d4vid.mods.genesis.server

import com.mojang.brigadier.Command
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
    return Commands.literal("give")
        .requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
        .then(
            Commands.argument("item", StringArgumentType.word())
                .suggests(CustomItemSuggestionProvider())
                .executes { context ->
                    val player = context.source.playerOrException
                    val item = getItemFromArg(context, "item")

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
    return Commands.literal("enable")
        .requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
        .then(
            Commands.argument("target", EntityArgument.player())
                .executes { context ->
                    val target = EntityArgument.getPlayer(context, "target")

                    PvpProtectionData.removeProtection(target.uuid)
                    context.source.sendSuccess(
                        { Component.literal("Removed combat protection from ${target.name.string}.") },
                        false
                    )

                    Command.SINGLE_SUCCESS
                }
        )
}

private fun protectionDisableCommand(): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("disable")
        .executes { context ->
            val player = context.source.playerOrException

            if (!PvpProtectionData.isProtected(player.uuid)) {
                context.source.sendFailure(Component.literal("You aren't combat protected."))
            } else {
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
