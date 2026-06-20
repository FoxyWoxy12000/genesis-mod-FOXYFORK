package dev.d4vid.mods.genesis.server.item;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Unit;

public class CustomItemBuilder {
    public static ItemStack build(Item baseItem, Component name, Identifier model) {
        ItemStack stack = new ItemStack(baseItem);
        stack.set(DataComponents.ITEM_MODEL, model);
        stack.set(DataComponents.CUSTOM_NAME, name);
        stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        return stack;
    }
}
