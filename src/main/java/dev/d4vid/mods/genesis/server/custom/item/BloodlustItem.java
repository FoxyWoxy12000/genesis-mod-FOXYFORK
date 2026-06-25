package dev.d4vid.mods.genesis.server.custom.item;

import dev.d4vid.mods.genesis.server.custom.item.util.ItemEnchantmentsBuilder;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

public class BloodlustItem extends GenesisItem {
    private static final String KILLS_TAG = "killedPlayers";
    private static final int[] LEVEL_KILLS = {0, 1, 3, 5, 9};
    private static final int BLOODLUST_RED = 0xAA0000;
    private static final Component DISPLAY_NAME = Component
        .literal("Bloodlust")
        .withStyle(s -> s.withItalic(false).withBold(true).withColor(BLOODLUST_RED));
    private static final Component LEVELED_UP = Component
        .literal("Bloodlust has leveled up.")
        .withStyle(s -> s.withBold(true).withColor(BLOODLUST_RED));

    public BloodlustItem() {
        super("bloodlust", Items.DIAMOND_SWORD, DISPLAY_NAME);
    }

    @Override
    protected void build(RegistryAccess registries, ItemStack item) {
        item.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);

        enchant(registries, item, 0);
        applyLore(item, 0, 0);
    }

    @Override
    public void initialize() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!(entity instanceof ServerPlayer victim)) {
                return;
            }

            Entity attackerEntity = source.getEntity();
            if (!(attackerEntity instanceof ServerPlayer attacker)) {
                return;
            }

            ItemStack item = attacker.getMainHandItem();
            if (!this.is(item)) {
                return;
            }

            this.addKill(item, attacker, victim);
        });
    }

    private void addKill(ItemStack item, ServerPlayer attacker, ServerPlayer victim) {
        RegistryAccess registries = attacker.level().registryAccess();

        ListTag kills = getKills(item);
        int killCount = kills.size();
        int initialLevel = getLevel(killCount);

        kills.add(StringTag.valueOf(victim.getStringUUID()));
        killCount++;

        int newLevel = getLevel(killCount);

        enchant(registries, item, newLevel);
        applyLore(item, newLevel, killCount);

        if (newLevel != initialLevel) {
            attacker.sendSystemMessage(LEVELED_UP);
        }

        setKills(item, kills);
    }

    private void enchant(RegistryAccess registries, ItemStack item, int level) {
        new ItemEnchantmentsBuilder(registries)
            .add(Enchantments.SHARPNESS, level + 2)
            .add(Enchantments.FIRE_ASPECT, 2)
            .add(Enchantments.LOOTING, 3)
            .add(Enchantments.SWEEPING_EDGE, 3)
            .enchant(item);
    }

    private void applyLore(ItemStack item, int level, int killCount) {
        item.set(DataComponents.LORE, new ItemLore(List.of(
            Component.literal(""),
            Component.literal("Gets stronger with kills.")
                .withStyle(s -> s.withItalic(false).withBold(true).withColor(BLOODLUST_RED)),
            Component.literal(getLevelLore(level, killCount))
                .withStyle(s -> s.withItalic(false).withColor(BLOODLUST_RED)),
            Component.literal("Fallen Souls: " + killCount)
                .withStyle(s -> s.withItalic(false).withColor(BLOODLUST_RED - 0x220000))
        )));
    }

    private String getLevelLore(int level, int killCount) {
        if (level == LEVEL_KILLS.length - 1) {
            return "Level MAX";
        }

        int requiredKills = LEVEL_KILLS[level + 1] - killCount;

        return "Level " + level + " | Level up in " + requiredKills + " kill" + (requiredKills == 1 ? "" : "s");
    }

    private int getLevel(int killCount) {
        int level = 0;

        for (int i = 0; i < LEVEL_KILLS.length; i++) {
            if (killCount >= LEVEL_KILLS[i]) {
                level = i;
            }
        }

        return level;
    }

    private ListTag getKills(ItemStack item) {
        return getCustomData(item).getListOrEmpty(KILLS_TAG);
    }

    private void setKills(ItemStack item, ListTag kills) {
        CompoundTag tag = new CompoundTag();
        tag.put(KILLS_TAG, kills);
        setCustomData(item, tag);
    }
}
