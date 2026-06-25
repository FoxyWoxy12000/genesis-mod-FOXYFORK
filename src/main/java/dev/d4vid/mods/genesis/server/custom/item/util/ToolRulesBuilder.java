package dev.d4vid.mods.genesis.server.custom.item.util;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ToolRulesBuilder {
    private final Registry<Block> registry;
    private final ArrayList<Tool.Rule> rules;

    public ToolRulesBuilder(RegistryAccess registries) {
        registry = registries.lookupOrThrow(Registries.BLOCK);
        rules = new ArrayList<>();
    }

    public List<Tool.Rule> build() {
        return rules;
    }

    public ToolRulesBuilder add(TagKey<Block> key, Optional<Float> speed, Optional<Boolean> correctForDrops) {
        rules.add(new Tool.Rule(registry.getOrThrow(key), speed, correctForDrops));
        return this;
    }
}
