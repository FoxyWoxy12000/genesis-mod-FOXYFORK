package dev.d4vid.mods.genesis.server.mixin.custom.item;

import dev.d4vid.mods.genesis.server.event.GenesisCustomItemEvents;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "handlePlayerAction", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V",
        shift = At.Shift.AFTER
    ), cancellable = true)
    private void genesis$handlePlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo callback) {
        if (packet.getAction() != ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            return;
        }

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        boolean result = GenesisCustomItemEvents.INSTANCE.getALLOW_ITEM_SWAP().invoker().allowItemSwap(player, stack);

        if (!result) {
            callback.cancel();
        }
    }
}
