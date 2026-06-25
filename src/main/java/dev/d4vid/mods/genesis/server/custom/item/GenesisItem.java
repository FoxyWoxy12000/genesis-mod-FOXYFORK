package dev.d4vid.mods.genesis.server.custom.item;

import dev.d4vid.mods.genesis.server.Genesis;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public abstract class GenesisItem {
    protected final Identifier identifier;
    protected final ItemStack baseItem;

    public static Identifier getId(ItemStack stack) {
        return stack.get(DataComponents.ITEM_MODEL);
    }

    protected GenesisItem(String name, Item baseItem, Component displayName) {
        identifier = Identifier.fromNamespaceAndPath(Genesis.MOD_ID, name);

        ItemStack stack = new ItemStack(baseItem);
        stack.set(DataComponents.ITEM_MODEL, identifier);
        stack.set(DataComponents.CUSTOM_NAME, displayName);

        this.baseItem = stack;
    }

    protected abstract void build(RegistryAccess registries, ItemStack stack);

    public void initialize() {
    }

    protected CompoundTag getCustomData(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    protected void setCustomData(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public Identifier getId() {
        return identifier;
    }

    public ItemStack assemble(RegistryAccess registries) {
        ItemStack item = baseItem.copy();
        build(registries, item);
        return item;
    }

    public boolean is(ItemStack stack) {
        return identifier.equals(getId(stack));
    }
}
