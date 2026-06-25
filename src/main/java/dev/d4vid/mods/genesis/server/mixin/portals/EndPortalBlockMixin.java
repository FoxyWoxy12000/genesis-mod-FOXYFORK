package dev.d4vid.mods.genesis.server.mixin.portals;

import dev.d4vid.mods.genesis.server.event.GenesisPortalEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    @Inject(method = "getPortalDestination", at = @At("HEAD"), cancellable = true)
    private void genesis$getPortalDestination(ServerLevel level, Entity entity, BlockPos blockPos, CallbackInfoReturnable<TeleportTransition> callback) {
        boolean result = GenesisPortalEvents.INSTANCE.getALLOW_END().invoker().allowEnd(level);

        if (!result) {
            callback.setReturnValue(null);
        }
    }
}
