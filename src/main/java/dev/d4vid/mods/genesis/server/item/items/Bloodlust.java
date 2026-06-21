package dev.d4vid.mods.genesis.server.item.items;

import dev.d4vid.mods.genesis.server.item.CustomItem;
import dev.d4vid.mods.genesis.server.item.CustomItemBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public class Bloodlust implements CustomItem {

    @Override
    public ItemStack create(RegistryAccess registries) {
        Component name = Component.literal("Bloodlust").withStyle(s -> s
            .withItalic(false)
            .withBold(true)
            .withColor(0xAA0000)
        );
        ItemStack stack = CustomItemBuilder.build(Items.DIAMOND_SWORD, name, getModel());

        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        Holder<Enchantment> sharpness = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.SHARPNESS);
        Holder<Enchantment> fire = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.FIRE_ASPECT);
        Holder<Enchantment> loot = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.LOOTING);
        Holder<Enchantment> sweep = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.SWEEPING_EDGE);

        enchantments.set(sharpness, 2);
        enchantments.set(fire, 2);
        enchantments.set(loot, 3);
        enchantments.set(sweep, 3);
        stack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());

        return stack;
    }
    @Override
    public Identifier getModel() {
        return Identifier.tryParse("genesis:bloodlust");
    }

    private static CompoundTag getData(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag();
    }

    private static void setData(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void onKill(ItemStack stack,ServerPlayer attacker, ServerPlayer killed, RegistryAccess registries) {
        CompoundTag tag = getData(stack);

        ListTag killedList = tag.contains("killedPlayers")
            ? tag.getList("killedPlayers").orElse(new ListTag())
            : new ListTag();

        String killedUUID = killed.getUUID().toString();
        for (int i = 0; i < killedList.size(); i++) {
            String stored = killedList.get(i).asString().orElse("");
            if (stored.equals(killedUUID) || stored.equals("\"" + killedUUID + "\"")) {
                return;
            }
        }

        killedList.add(StringTag.valueOf(killedUUID));
        tag.put("killedPlayers", killedList);

        int kills = killedList.size();
        int sharpnessLevel = kills >= 9 ? 6 : kills >= 5 ? 5 : kills >= 3 ? 4 : kills >= 1 ? 3 : 2;

        Holder<Enchantment> sharpness = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.SHARPNESS);
        Holder<Enchantment> fire = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.FIRE_ASPECT);
        Holder<Enchantment> loot = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.LOOTING);
        Holder<Enchantment> sweep = registries.lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.SWEEPING_EDGE);

        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(sharpness, sharpnessLevel);
        enchantments.set(fire, 2);
        enchantments.set(loot, 3);
        enchantments.set(sweep, 3);
        stack.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());

        if (kills == 1 || kills == 3 || kills == 5 || kills == 9) {
            attacker.sendSystemMessage(
                Component.literal("Bloodlust Has Leveled Up!").withStyle(s -> s
                    .withItalic(false)
                    .withBold(true)
                    .withColor(0xAA0000)
                )
            );
        }
        //System.out.println("Bloodlust tag on hit: " + tag);
        updateLore(stack, kills);
        setData(stack, tag);
    }
    private static void updateLore(ItemStack stack, int kills) {
        List<Component> lines = new ArrayList<>();

        String killsText;
        if (kills >= 9) {
            killsText = "MAX Level";
        } else {
            int next = kills >= 5 ? 9 : kills >= 3 ? 5 : kills >= 1 ? 3 : 1;
            killsText = kills + " unique kills | Next level: " + next;
        }

        lines.add(Component.literal(killsText).withStyle(s ->s
            .withItalic(false)
            .withColor(0xAA0000)
        ));

        stack.set(DataComponents.LORE, new ItemLore(lines));
    }
    @Override
    public String getCommandName() {
        return "bloodlust";
    }
}

