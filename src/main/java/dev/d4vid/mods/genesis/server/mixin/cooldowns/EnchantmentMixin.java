package dev.d4vid.mods.genesis.server.mixin.cooldowns;

import dev.d4vid.mods.genesis.server.event.GenesisCooldownEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "method_75252", at = @At("HEAD"), cancellable = true)
    private static void genesis$onLunge(ServerLevel level, int power, EnchantedItemInUse itemInUse, Entity entity, EnchantmentEntityEffect effect, CallbackInfo callback) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        boolean result = GenesisCooldownEvents.INSTANCE.getALLOW_LUNGE().invoker().allowLunge(level, power, itemInUse, player, effect);

        if (!result) {
            callback.cancel();
        }
    }
}
