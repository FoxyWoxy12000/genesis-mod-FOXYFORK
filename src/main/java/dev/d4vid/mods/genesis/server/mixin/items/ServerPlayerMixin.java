package dev.d4vid.mods.genesis.server.mixin.items;

import dev.d4vid.mods.genesis.server.event.GenesisItemEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "doCloseContainer", at = @At("HEAD"))
    private void genesis$doCloseContainer(CallbackInfo callback) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        GenesisItemEvents.INSTANCE.getINVENTORY_CLOSE().invoker().inventoryClose(player);
    }

    @Inject(method = "drop", at = @At("RETURN"))
    private void genesis$drop(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> callback) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        GenesisItemEvents.INSTANCE.getINVENTORY_CHANGE().invoker().inventoryChange(player);
    }
}
