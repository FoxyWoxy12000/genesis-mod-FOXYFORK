package dev.d4vid.mods.genesis.server.item;

import dev.d4vid.mods.genesis.server.item.items.Bloodlust;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomItems {
    public static final Map<String, CustomItem> REGISTERY = new LinkedHashMap<>();

    static {
        register(new Bloodlust());
    }

    private static void register(CustomItem item) {
        REGISTERY.put(item.getCommandName(), item);
    }
}
