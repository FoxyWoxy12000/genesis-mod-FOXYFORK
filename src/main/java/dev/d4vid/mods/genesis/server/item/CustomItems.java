package dev.d4vid.mods.genesis.server.item;

import dev.d4vid.mods.genesis.server.item.items.Bloodlust;
import dev.d4vid.mods.genesis.server.item.items.MegaDrill;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomItems {
    public static final Map<String, CustomItem> REGISTERY = new LinkedHashMap<>();

    static {
        register(new Bloodlust());
        register(new MegaDrill());
    }

    private static void register(CustomItem item) {
        REGISTERY.put(item.getCommandName(), item);
    }

    public static boolean isCustomItem(ItemStack stack) {
        Identifier model = stack.get(DataComponents.ITEM_MODEL);
        if (model == null) return false;
        return REGISTERY.values().stream()
            .anyMatch(item -> item.getModel().equals(model));
    }
}
