package dev.d4vid.mods.genesis.server.mixin.items;

import dev.d4vid.mods.genesis.server.event.GenesisItemEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
class LivingEntityMixin {
    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void genesis$checkTotemDeathProtection(DamageSource source, CallbackInfoReturnable<Boolean> callback) {
        boolean result = GenesisItemEvents.INSTANCE.getALLOW_TOTEM().invoker().allowTotem(source);

        if (!result) {
            callback.setReturnValue(false);
        }
    }
}
