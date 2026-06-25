package dev.d4vid.mods.genesis.server.mixin.pvp;

import dev.d4vid.mods.genesis.server.event.GenesisCombatEvents;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@SuppressWarnings("unused")
@Mixin(EndCrystal.class)
public class EndCrystalMixin {
    @ModifyArg(
        method = "hurtServer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)V"
        ),
        index = 6
    )
    private float genesis$explode(float radius) {
        Float result = GenesisCombatEvents.INSTANCE.getMODIFY_END_CRYSTAL_EXPLOSION_RADIUS().invoker().modifyEndCrystalExplosionRadius(radius);

        return result == null ? radius : result;
    }
}
