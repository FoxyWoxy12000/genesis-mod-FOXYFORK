package dev.d4vid.mods.genesis.server.mixin.event;

import dev.d4vid.mods.genesis.server.event.PlayerItemUseCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@SuppressWarnings("unused")
@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    private void genesis$useItem(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callback) {
        InteractionResult result = PlayerItemUseCallback.Companion.getEVENT().invoker().interact(player, level, stack, hand, null);

        if (result != InteractionResult.PASS) {
            callback.setReturnValue(result);
            player.getCooldowns().removeCooldown(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void genesis$useItemOn(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> callback) {
        InteractionResult result = PlayerItemUseCallback.Companion.getEVENT().invoker().interact(player, level, stack, hand, blockHit);

        if (result != InteractionResult.PASS) {
            callback.setReturnValue(result);
            player.getCooldowns().removeCooldown(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        }
    }

    public ServerPlayer player;
    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void genisis$destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (player.gameMode.getGameModeForPlayer() == GameType.SURVIVAL) {
            if (player.level().getBlockState(pos).is(Blocks.SPAWNER)) {
                info.cancel();
            }
        }
    }
}
