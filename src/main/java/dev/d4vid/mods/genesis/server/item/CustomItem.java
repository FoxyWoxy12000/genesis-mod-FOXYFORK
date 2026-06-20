package dev.d4vid.mods.genesis.server.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import net.minecraft.core.RegistryAccess;

public interface CustomItem {
    ItemStack create(RegistryAccess registries);
    Identifier getModel();
    String getCommandName();
}
