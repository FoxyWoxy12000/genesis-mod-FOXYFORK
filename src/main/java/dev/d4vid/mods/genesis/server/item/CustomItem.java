package dev.d4vid.mods.genesis.server.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface CustomItem {
    ItemStack create();
    ResourceLocation getModel();
}
