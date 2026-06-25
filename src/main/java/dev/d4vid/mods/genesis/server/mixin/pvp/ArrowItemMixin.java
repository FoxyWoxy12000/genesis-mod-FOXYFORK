package dev.d4vid.mods.genesis.server.mixin.pvp;

import dev.d4vid.mods.genesis.server.event.GenesisCombatEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@SuppressWarnings("unused")
@Mixin(ArrowItem.class)
public class ArrowItemMixin {
    @ModifyArg(
        method = "createArrow",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/arrow/Arrow;<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"
        ),
        index = 2
    )
    private ItemStack genesis$createArrow(ItemStack stack) {
        return handle(stack);
    }

    @ModifyArg(
        method = "asProjectile",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/arrow/Arrow;<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"
        ),
        index = 4
    )
    private ItemStack genesis$asProjectile(ItemStack stack) {
        return handle(stack);
    }

    private ItemStack handle(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) {
            return stack;
        }

        boolean allowed = contents.potion().map(holder ->
            GenesisCombatEvents.INSTANCE.getALLOW_ARROW_POTION().invoker().allowArrowPotion(holder)
        ).orElse(true);

        return allowed ? stack : new ItemStack(Items.ARROW);
    }
}
