package dev.d4vid.mods.genesis.server.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.d4vid.mods.genesis.server.config.GenesisConfig
import dev.d4vid.mods.genesis.server.pvp.CombatProtectionHandler
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

fun genesisCommand(
    config: GenesisConfig,
    combatProtection: CombatProtectionHandler
): LiteralArgumentBuilder<CommandSourceStack> {
    return Commands.literal("genesis")
        .then(reloadCommand(config))
        .then(protectionCommand(config, combatProtection))
        .then(giveCommand())
}
