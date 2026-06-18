package dev.d4vid.mods.genesis.server.mixin.event;

import dev.d4vid.mods.genesis.server.event.PlayerAttackPlayerCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "hurtOrSimulate", at = @At("HEAD"))
    private void genesis$hurtOrSimulate(DamageSource source, float damage, CallbackInfoReturnable<Boolean> callback) {
        Entity dst = (Entity) (Object) this;
        if (!(dst instanceof ServerPlayer victim)) {
            return;
        }

        Entity src = source.getEntity();
        if (!(src instanceof ServerPlayer attacker)) {
            return;
        }

        PlayerAttackPlayerCallback.Companion.getEVENT().invoker().interact(attacker, victim, damage);
    }
}
