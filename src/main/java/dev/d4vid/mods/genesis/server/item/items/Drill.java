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
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.component.Tool;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.HolderSet;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

public class Drill implements CustomItem {

    @Override
    public ItemStack create(RegistryAccess registries) {
        Component name = Component.literal("Drill").withStyle(s -> s
            .withItalic(false)
            .withBold(true)
            //.withColor(0x64C4FF)
        );
        ItemStack stack = CustomItemBuilder.build(Items.DIAMOND_PICKAXE, name, getModel(), false);

        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        Holder<Enchantment> efficency = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.EFFICIENCY);
        Holder<Enchantment> fortune = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.FORTUNE);

        enchantments.set(efficency, 5);
        enchantments.set(fortune, 3);

        stack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());

        HolderSet<Block> pickaxeBlocks = registries.lookupOrThrow(Registries.BLOCK)
            .getOrThrow(BlockTags.MINEABLE_WITH_PICKAXE);
        HolderSet<Block> shovelBlocks = registries.lookupOrThrow(Registries.BLOCK)
            .getOrThrow(BlockTags.MINEABLE_WITH_SHOVEL);
        HolderSet<Block> axeBlocks = registries.lookupOrThrow(Registries.BLOCK)
            .getOrThrow(BlockTags.MINEABLE_WITH_AXE);

        Tool tool = new Tool(
            List.of(
                new Tool.Rule(pickaxeBlocks, Optional.of(9.0f), Optional.of(true)),
                new Tool.Rule(shovelBlocks, Optional.of(9.0f), Optional.of(true)),
                new Tool.Rule(axeBlocks, Optional.of(9.0f), Optional.of(true))
            ),
            1.0f,
            1,
            true
        );
        stack.set(DataComponents.TOOL, tool);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.literal("Press [F] to toggle Silk Touch / Fortune").withStyle(s -> s
            .withItalic(true).withColor(0x888888)));
        stack.set(DataComponents.LORE, new ItemLore(lore));

        return stack;
    }
    @Override
    public Identifier getModel() {
        return Identifier.tryParse("genesis:drill");
    }

    private static CompoundTag getData(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag();
    }

    private static void setData(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static boolean isSilkTouch(ItemStack stack) {
        CompoundTag tag = getData(stack);
        return tag.getBoolean("silkTouch").orElse(false);
    }

    public static void toggleMode(ItemStack stack, RegistryAccess registry) {
        CompoundTag tag = getData(stack);
        boolean currentlySilk = tag.getBoolean("silkTouch").orElse(false);
        tag.putBoolean("silkTouch", !currentlySilk);
        setData(stack, tag);
        applyEnchantments(stack, !currentlySilk, registry);
    }

    private static void applyEnchantments(ItemStack stack, boolean silkTouch, RegistryAccess registries) {
        Holder<Enchantment> efficiency = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.EFFICIENCY);
        Holder<Enchantment> silk = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.SILK_TOUCH);
        Holder<Enchantment> fortune = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.FORTUNE);

        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(efficiency, 5);
        if (silkTouch) {
            enchantments.set(silk, 1);
        } else {
            enchantments.set(fortune, 3);
        }
        stack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
    }

    @Override
    public String getCommandName() {
        return "drill";
    }
}
