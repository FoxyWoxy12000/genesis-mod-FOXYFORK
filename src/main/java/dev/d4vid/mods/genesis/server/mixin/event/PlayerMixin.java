package dev.d4vid.mods.genesis.server.mixin.event;

import dev.d4vid.mods.genesis.server.event.PlayerAttackPlayerCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void genesis$hurtOrSimulate(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> callback) {
        if (!callback.getReturnValueZ()) {
            return;
        }

        Player victim = (Player) (Object) this;
        Entity src = source.getEntity();

        if (src instanceof AbstractArrow arrow) {
            src = arrow.getOwner();
        }

        if (!(src instanceof Player attacker)) {
            return;
        }

        PlayerAttackPlayerCallback.Companion.getEVENT().invoker().interact(attacker, victim, damage);
    }
}
