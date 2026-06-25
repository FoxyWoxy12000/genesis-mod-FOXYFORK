package dev.d4vid.mods.genesis.server.mixin.pvp;

import dev.d4vid.mods.genesis.server.event.GenesisCombatEvents;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@SuppressWarnings("unused")
@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {
    @ModifyArg(
        method = "explode",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;Lnet/minecraft/world/phys/Vec3;FZLnet/minecraft/world/level/Level$ExplosionInteraction;)V"
        ),
        index = 4
    )
    private float genesis$explode(float radius) {
        Float result = GenesisCombatEvents.INSTANCE.getMODIFY_RESPAWN_ANCHOR_EXPLOSION_RADIUS().invoker().modifyRespawnAnchorExplosionRadius(radius);

        return result == null ? radius : result;
    }
}
