package dev.d4vid.mods.genesis.server.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack

fun registerCommand(command: LiteralArgumentBuilder<CommandSourceStack>) {
    CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
        dispatcher.register(command)
    }
}
