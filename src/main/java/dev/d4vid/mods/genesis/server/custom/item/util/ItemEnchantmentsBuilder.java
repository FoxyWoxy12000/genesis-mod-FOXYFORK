package dev.d4vid.mods.genesis.server.custom.item.util;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class ItemEnchantmentsBuilder {
    private final Registry<Enchantment> registry;
    private final ItemEnchantments.Mutable enchantments;

    public ItemEnchantmentsBuilder(RegistryAccess registries) {
        registry = registries.lookupOrThrow(Registries.ENCHANTMENT);
        enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
    }

    public void enchant(ItemStack item) {
        item.set(DataComponents.ENCHANTMENTS, build());
    }

    public ItemEnchantments build() {
        return enchantments.toImmutable();
    }

    public ItemEnchantmentsBuilder add(ResourceKey<Enchantment> key, int strength) {
        enchantments.set(registry.getOrThrow(key), strength);
        return this;
    }
}
