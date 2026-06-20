package dev.d4vid.mods.genesis.server.mixin.item;

import dev.d4vid.mods.genesis.server.item.CustomItem;
import dev.d4vid.mods.genesis.server.item.CustomItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentMenu.class)
public class EnchantmentProtectionMixin {

    @Inject(method = "slotsChanged", at = @At("HEAD"))
    private void genesis$slotsChanged(Container container, CallbackInfo info) {
        EnchantmentMenu menu = (EnchantmentMenu) (Object) this;
        ItemStack input = menu.getSlot(0).getItem();

        if (CustomItems.isCustomItem(input)) {
            menu.getSlot(1).set(ItemStack.EMPTY);
            menu.getSlot(2).set(ItemStack.EMPTY);
            menu.getSlot(3).set(ItemStack.EMPTY);
        }
    }
}
