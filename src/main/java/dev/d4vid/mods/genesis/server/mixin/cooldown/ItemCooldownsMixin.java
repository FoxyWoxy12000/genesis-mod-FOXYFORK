package dev.d4vid.mods.genesis.server.mixin.cooldown;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(ItemCooldowns.class)
public class ItemCooldownsMixin {
    @Inject(method = "isOnCooldown", at = @At("HEAD"), cancellable = true)
    private void genesis$isOnCooldown(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
        if (stack.is(ItemTags.SPEARS)) {
            callback.setReturnValue(false);
        }
    }
}
