package dev.d4vid.mods.genesis.server.mixin.resourcePack;

import dev.d4vid.mods.genesis.server.GenesisConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import static net.minecraft.network.protocol.common.ServerboundResourcePackPacket.Action.DECLINED;
import static net.minecraft.network.protocol.common.ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED;

 @Mixin(ServerCommonPacketListenerImpl.class)
    public class ResourcePackResponseMixin {

        public ServerPlayer player;

    @Inject(method = "handleResourcePackResponse", at = @At("HEAD"))
    private void onPackResponse(ServerboundResourcePackPacket packet, CallbackInfo info) {

        if (!(((Object) this) instanceof ServerGamePacketListenerImpl impl)) return;
        ServerPlayer player = impl.player;
        MinecraftServer server = player.level().getServer();
        switch (packet.action()) {
            case DECLINED -> {
                System.out.println(player.getName().getString() + " declined");
                if (GenesisConfig.INSTANCE.isResourcePackKickOnDecline()) {
                    impl.disconnect(Component.literal("You must accept the resource pack to play GOOBER, also your choice will be remembered."));
                }
            }
            case SUCCESSFULLY_LOADED -> {
                System.out.println(player.getName().getString() + " accepted");
                ResourcePackPlayerData data = ResourcePackPlayerData.get(server);
                ResourcePackPlayerData.get(server).acceptedPlayers.add(player.getUUID());
                data.save();
            }
            default -> {}
        }
    }
}

