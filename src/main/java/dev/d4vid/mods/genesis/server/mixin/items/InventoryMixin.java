package dev.d4vid.mods.genesis.server.mixin.items;

import dev.d4vid.mods.genesis.server.event.GenesisItemEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"unused", "UNCHECKED_CAST"})
@Mixin(Inventory.class)
public class InventoryMixin {
    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
    private void genesis$add(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
        Inventory inventory = (Inventory) (Object) this;
        ServerPlayer player = (ServerPlayer) inventory.player;

        GenesisItemEvents.INSTANCE.getINVENTORY_ADD().invoker().inventoryAdd(player);
    }

    @Inject(method = "setItem", at = @At("RETURN"))
    private void genesis$setItem(int slot, ItemStack stack, CallbackInfo callback) {
        if (stack.isEmpty()) {
            return;
        }

        Inventory inventory = (Inventory) (Object) this;
        ServerPlayer player = (ServerPlayer) inventory.player;

        GenesisItemEvents.INSTANCE.getINVENTORY_CHANGE().invoker().inventoryChange(player);
    }
}
