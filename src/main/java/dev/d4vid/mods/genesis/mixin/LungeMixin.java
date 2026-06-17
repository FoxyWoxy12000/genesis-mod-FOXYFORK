package dev.d4vid.mods.genesis.mixin;

import dev.d4vid.mods.genesis.server.cooldown.CooldownManager;
import dev.d4vid.mods.genesis.server.cooldown.CooldownType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Duration;

@SuppressWarnings("unused")
@Mixin(Enchantment.class)
public class LungeMixin {
    private final CooldownManager cooldown = new CooldownManager(CooldownType.Lunge);

    @Inject(method = "doLunge", at = @At("HEAD"), cancellable = true)
    private void onLunge(ServerLevel serverLevel, int i, EnchantedItemInUse item, Entity entity, CallbackInfo info) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = item.itemStack();
        Duration duration = cooldown.apply(player);

        if (duration != null) {
            float tickRate = serverLevel.getServer().tickRateManager().tickrate();
            System.out.println(player.getCooldowns().getCooldownGroup(stack));

            player.getCooldowns().addCooldown(stack, (int) (duration.toSeconds() * tickRate));

            return;
        }

        info.cancel();
    }
}
