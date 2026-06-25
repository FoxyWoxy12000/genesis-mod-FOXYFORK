package dev.d4vid.mods.genesis.server.mixin.recipes;

import dev.d4vid.mods.genesis.server.event.GenesisRecipeEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(MerchantOffers.class)
class MerchantOffersMixin {
    @Inject(method = "getRecipeFor", at = @At("RETURN"), cancellable = true)
    private void genesis$getRecipeFor(ItemStack stack, ItemStack stack2, int i, CallbackInfoReturnable<MerchantOffer> callback) {
        MerchantOffer offer = callback.getReturnValue();

        if (offer == null) {
            return;
        }

        ItemStack resultItem = offer.getResult();
        boolean result = GenesisRecipeEvents.INSTANCE.getALLOW().invoker().allow(new ItemStack[]{stack, stack2}, resultItem);

        if (!result) {
            callback.setReturnValue(null);
        }
    }
}
