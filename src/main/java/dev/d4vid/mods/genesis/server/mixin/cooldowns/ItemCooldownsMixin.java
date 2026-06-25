package dev.d4vid.mods.genesis.server.mixin.cooldowns;

import dev.d4vid.mods.genesis.server.event.GenesisCooldownEvents;
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
        boolean result = GenesisCooldownEvents.INSTANCE.getALLOW_ITEM_COOLDOWN().invoker().allowCooldown(stack);

        if (!result) {
            callback.setReturnValue(false);
        }
    }
}
