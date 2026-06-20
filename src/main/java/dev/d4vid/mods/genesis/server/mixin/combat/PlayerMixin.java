package dev.d4vid.mods.genesis.server.mixin.combat;

import dev.d4vid.mods.genesis.server.GenesisConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(Player.class)
public class PlayerMixin {
    private static final GenesisConfig config = GenesisConfig.INSTANCE;

    @Inject(method = "canHarmPlayer", at = @At("HEAD"), cancellable = true)
    private void genesis$canHarmPlayer(Player attacker, CallbackInfoReturnable<Boolean> callback) {
        ServerPlayer victim = (ServerPlayer) (Object) this;

        Team team = victim.getTeam();
        Team team2 = attacker.getTeam();

        if ((team != null && config.isTeamFriendly(team)) || (team2 != null && config.isTeamFriendly(team2))) {
            callback.setReturnValue(false);
        }

        if (config.isCombatSpawnProtectionEnabled()) {
            double radius = config.getCombatSpawnProtectionRadius();
            double dx = victim.getX() - config.getCombatSpawnProtectionX();
            double dz = victim.getZ() - config.getCombatSpawnProtectionZ();

            if ((dx * dx + dz * dz) <= (radius * radius)) {
                ((ServerPlayer) attacker).sendSystemMessage(
                    Component.literal("This player is spawn protected.").withStyle(
                        ChatFormatting.RED
                    ), true
                );

                callback.setReturnValue(false);
            }
        }
    }
}
