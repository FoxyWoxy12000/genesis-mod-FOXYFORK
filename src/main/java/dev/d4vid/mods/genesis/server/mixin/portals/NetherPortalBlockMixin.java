package dev.d4vid.mods.genesis.server.mixin.portals;

import dev.d4vid.mods.genesis.server.GenesisConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {
    @Inject(method = "getPortalDestination", at = @At("HEAD"), cancellable = true)
    private void genesis$getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos, CallbackInfoReturnable<TeleportTransition> callback) {
        if (serverLevel.dimension() == ServerLevel.OVERWORLD && GenesisConfig.INSTANCE.isNetherDisabled()) {
            callback.setReturnValue(null);
        }
    }
}
