package dev.d4vid.mods.genesis.server.mixin.portals;

import dev.d4vid.mods.genesis.server.GenesisConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(EnderEyeItem.class)
public class EnderEyeItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void genesis$use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callback) {
        if (GenesisConfig.INSTANCE.isEndDisabled()) {
            callback.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void genesis$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> callback) {
        if (!GenesisConfig.INSTANCE.isEndDisabled()) {
            return;
        }

        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);

        if (blockState.is(Blocks.END_PORTAL_FRAME)) {
            callback.setReturnValue(InteractionResult.FAIL);
        }
    }
}
