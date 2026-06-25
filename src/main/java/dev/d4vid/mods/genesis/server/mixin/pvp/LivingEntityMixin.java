package dev.d4vid.mods.genesis.server.mixin.pvp;

import dev.d4vid.mods.genesis.server.event.GenesisCombatEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void genesis$hurtServer(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> callback) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof TamableAnimal animal) || !animal.isTame()) {
            return;
        }

        boolean result = GenesisCombatEvents.INSTANCE.getALLOW_PET_DAMAGE().invoker().allowPetDamage(level, animal, source, damage);

        if (!result) {
            callback.setReturnValue(false);
        }
    }
}
