package dev.d4vid.mods.genesis.server.mixin.cooldown;

import dev.d4vid.mods.genesis.server.cooldown.CooldownManager;
import dev.d4vid.mods.genesis.server.cooldown.CooldownType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Duration;

@SuppressWarnings("unused")
@Mixin(Enchantment.class)
public class EnchantmentMixin {
    private static final CooldownManager cooldown = new CooldownManager(CooldownType.Lunge);

    @Inject(method = "method_75252", at = @At("HEAD"), cancellable = true)
    private static void genesis$doLunge(ServerLevel serverLevel, int lungePower, EnchantedItemInUse item, Entity entity, EnchantmentEntityEffect effect, CallbackInfo callback) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = item.itemStack();
        Duration duration = cooldown.apply(player);

        if (duration != null) {
            float tickRate = serverLevel.getServer().tickRateManager().tickrate();
            player.getCooldowns().addCooldown(stack, (int) (duration.toSeconds() * tickRate));

            return;
        }

        callback.cancel();
    }
}
