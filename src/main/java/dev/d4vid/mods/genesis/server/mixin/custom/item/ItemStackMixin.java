package dev.d4vid.mods.genesis.server.mixin.custom.item;

import dev.d4vid.mods.genesis.server.custom.item.GenesisItems;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void genesis$isEnchantable(CallbackInfoReturnable<Boolean> callback) {
        ItemStack stack = (ItemStack) (Object) this;

        if (GenesisItems.is(stack)) {
            callback.setReturnValue(false);
        }
    }
}
