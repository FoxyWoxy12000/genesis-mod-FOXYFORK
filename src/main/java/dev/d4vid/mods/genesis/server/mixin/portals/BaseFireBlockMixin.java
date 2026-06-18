package dev.d4vid.mods.genesis.server.mixin.portals;

import dev.d4vid.mods.genesis.server.GenesisConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {
    @Inject(method = "inPortalDimension", at = @At("HEAD"), cancellable = true)
    private static void genesis$inPortalDimension(Level level, CallbackInfoReturnable<Boolean> callback) {
        if (GenesisConfig.INSTANCE.isEndDisabled()) {
            callback.setReturnValue(level.dimension() == Level.NETHER);
        }
    }
}
