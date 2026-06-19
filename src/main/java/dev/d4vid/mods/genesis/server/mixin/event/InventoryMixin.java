package dev.d4vid.mods.genesis.server.mixin.event;

import dev.d4vid.mods.genesis.server.event.PlayerInventoryInteractCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(Inventory.class)
public class InventoryMixin {
    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
    private void genesis$add(int i, ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
        Inventory inventory = (Inventory) (Object) this;
        ServerPlayer player = (ServerPlayer) inventory.player;

        PlayerInventoryInteractCallback.Companion.getEVENT().invoker().interact(player);
    }
}
