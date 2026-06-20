package dev.d4vid.mods.genesis.server.item.items;

import dev.d4vid.mods.genesis.server.item.CustomItem;
import dev.d4vid.mods.genesis.server.item.CustomItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class MegaDrill implements CustomItem {

    @Override
    public ItemStack create(RegistryAccess registries) {
        Component name = Component.literal("Mega Drill").withStyle(s -> s
            .withItalic(false)
            .withBold(true)
            .withColor(0x64C4FF)
        );
        ItemStack stack = CustomItemBuilder.build(Items.NETHERITE_PICKAXE, name, getModel());

        Holder<Enchantment> efficency = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.EFFICIENCY);

        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(efficency, 10);
        stack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());

        return stack;
    }
    @Override
    public Identifier getModel() {
        return Identifier.tryParse("genesis:megadrill");
    }

    private static CompoundTag getData(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag();
    }

    private static void setData(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public String getCommandName() {
        return "megadrill";
    }
}
