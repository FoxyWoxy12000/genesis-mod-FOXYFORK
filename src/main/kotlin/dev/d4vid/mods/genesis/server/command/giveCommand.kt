package dev.d4vid.mods.genesis.server.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import dev.d4vid.mods.genesis.server.Genesis
import dev.d4vid.mods.genesis.server.custom.item.GenesisItems
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.permissions.Permissions
import net.minecraft.world.item.ItemStack

private val UNKNOWN_ITEM = DynamicCommandExceptionType { Component.literal("Unknown Genesis item '$it'") }
private fun itemGiven(target: ServerPlayer, stack: ItemStack, count: Int): Component {
    return Component.empty()
        .append(Component.literal("Gave $count "))
        .append(
            Component.literal("[")
                .append(stack.customName!!.copy())
                .append(Component.literal("]"))
                .withStyle { it.withHoverEvent(HoverEvent.ShowItem(stack)) }
        )
        .append(Component.literal(" to "))
        .append(target.displayName)
}

fun giveCommand(): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("give")
        .requires { source -> source.permissions().hasPermission(Permissions.COMMANDS_OWNER) }
        .then(
            Commands.argument("target", EntityArgument.players())
                .then(
                    Commands.argument("item", StringArgumentType.word())
                        .suggests(GenesisItemSuggestionProvider())
                        .executes { context ->
                            val targets = EntityArgument.getPlayers(context, "target")
                            val item = StringArgumentType.getString(context, "item")

                            for (target in targets) {
                                giveItem(context, target, item)
                            }

                            Command.SINGLE_SUCCESS
                        }
                        .then(
                            Commands.argument("count", IntegerArgumentType.integer())
                                .suggests(GenesisItemSuggestionProvider())
                                .executes { context ->
                                    val targets = EntityArgument.getPlayers(context, "target")
                                    val item = StringArgumentType.getString(context, "item")
                                    val count = IntegerArgumentType.getInteger(context, "count")

                                    for (target in targets) {
                                        giveItem(context, target, item, count)
                                    }

                                    Command.SINGLE_SUCCESS
                                }
                        )
                )
        )
}

private fun giveItem(
    context: CommandContext<CommandSourceStack>,
    target: ServerPlayer,
    itemName: String,
    count: Int = 1,
) {
    val id = Identifier.fromNamespaceAndPath(Genesis.MOD_ID, itemName)
    val item = GenesisItems.REGISTRY[id] ?: throw UNKNOWN_ITEM.create(itemName)
    val stack = item.assemble(context.source.registryAccess())

    val message = itemGiven(target, stack, count)
    target.inventory.add(stack)

    context.source.sendSuccess(
        { message },
        true,
    )
}
