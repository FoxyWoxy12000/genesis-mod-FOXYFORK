package dev.d4vid.mods.genesis.server.custom.item;

import dev.d4vid.mods.genesis.server.custom.item.util.ItemEnchantmentsBuilder;
import dev.d4vid.mods.genesis.server.custom.item.util.ToolRulesBuilder;
import dev.d4vid.mods.genesis.server.event.GenesisCustomItemEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;
import java.util.Optional;

public class MegaDrillItem extends GenesisItem {
    private static final int MEGA_DRILL_COLOR = 0x64C4FF;
    private static final int LORE_COLOR = 0x888888;
    private static final Component DISPLAY_NAME = Component
        .literal("Mega Drill")
        .withStyle(s -> s.withItalic(false).withBold(true).withColor(MEGA_DRILL_COLOR));

    public MegaDrillItem() {
        super("mega_drill", Items.NETHERITE_PICKAXE, DISPLAY_NAME);
    }

    public void toggle(ServerPlayer player, ItemStack item) {
        RegistryAccess registries = player.level().registryAccess();
        boolean silkTouch = !isSilkTouch(registries, item);

        enchant(registries, item, silkTouch);
        applyLore(item, silkTouch);

        player.sendSystemMessage(getToggleMessage(silkTouch), true);
    }

    @Override
    protected void build(RegistryAccess registries, ItemStack item) {
        item.set(DataComponents.TOOL, new Tool(
            new ToolRulesBuilder(registries)
                .add(BlockTags.MINEABLE_WITH_PICKAXE, Optional.of(9.0f), Optional.of(true))
                .add(BlockTags.MINEABLE_WITH_SHOVEL, Optional.of(9.0f), Optional.of(true))
                .add(BlockTags.MINEABLE_WITH_AXE, Optional.of(9.0f), Optional.of(true))
                .build(),
            1.0f,
            1,
            true
        ));

        enchant(registries, item, false);
        applyLore(item, false);
    }

    @Override
    public void initialize() {
        GenesisCustomItemEvents.INSTANCE.getALLOW_ITEM_SWAP().register((player, stack) -> {
            if (this.is(stack)) {
                this.toggle(player, stack);
                return false;
            }

            return true;
        });
    }

    private void enchant(RegistryAccess registries, ItemStack item, boolean silkTouch) {
        ItemEnchantmentsBuilder enchantments = new ItemEnchantmentsBuilder(registries)
            .add(Enchantments.EFFICIENCY, 12);

        if (silkTouch) {
            enchantments.add(Enchantments.SILK_TOUCH, 1);
        } else {
            enchantments.add(Enchantments.FORTUNE, 3);
        }

        enchantments.enchant(item);
    }

    private void applyLore(ItemStack item, boolean silkTouch) {
        item.set(DataComponents.LORE, new ItemLore(List.of(
            Component.literal(""),
            Component.literal("Press [F] to toggle:")
                .withStyle(s -> s.withItalic(true).withColor(LORE_COLOR)),
            Component.empty()
                .append(Component.literal("Silk Touch").withStyle(s -> s.withBold(silkTouch)))
                .append(Component.literal(" / "))
                .append(Component.literal("Fortune").withStyle(s -> s.withBold(!silkTouch)))
                .withStyle(s -> s.withItalic(true).withColor(LORE_COLOR))
        )));
    }

    private boolean isSilkTouch(RegistryAccess registries, ItemStack item) {
        Holder<Enchantment> silkTouch = registries
            .lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.SILK_TOUCH);

        return item.getEnchantments().getLevel(silkTouch) > 0;
    }

    private Component getToggleMessage(boolean silkTouch) {
        return Component.empty()
            .append(Component.literal("Mega Drill: ").withStyle(s -> s.withBold(true)))
            .append(Component.literal(silkTouch ? "Silk Touch" : "Fortune"))
            .withStyle(s -> s.withColor(MEGA_DRILL_COLOR));
    }
}
