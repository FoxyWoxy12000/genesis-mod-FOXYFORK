package dev.d4vid.mods.genesis.server.mixin.portals;

import dev.d4vid.mods.genesis.server.event.GenesisPortalEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(EnderEyeItem.class)
public class EnderEyeItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void genesis$use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callback) {
        handle(level, callback);
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void genesis$useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> callback) {
        handle(context.getLevel(), callback);
    }

    private void handle(Level level, CallbackInfoReturnable<InteractionResult> callback) {
        boolean result = GenesisPortalEvents.INSTANCE.getALLOW_END().invoker().allowEnd((ServerLevel) level);

        if (!result) {
            callback.setReturnValue(InteractionResult.FAIL);
        }
    }
}
