package dev.d4vid.mods.genesis.server

import dev.d4vid.mods.genesis.server.mixin.resourcePack.ResourcePackPlayerData
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket
import net.minecraft.server.level.ServerPlayer

fun registerResourcePackLoader() {
    ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
        sendResourcePack(handler.player)
    }
}

private fun sendResourcePack(player: ServerPlayer) {
    val url = GenesisConfig.getResourcePackUrl()
    val hash = GenesisConfig.getResourcePackSha1()
    val prompt = GenesisConfig.getResourcePackPrompt()
    val server = player.level().getServer() ?: return
    val data = ResourcePackPlayerData.get(server)

    if (data.acceptedPlayers.contains(player.uuid)) {
        return
    }

    player.connection.send(
        ClientboundResourcePackPushPacket(
            java.util.UUID.fromString("00000000-0000-0000-0000-000000000001"),
            url,
            hash,
            false,
            java.util.Optional.of(net.minecraft.network.chat.Component.literal(prompt))
        )
    )
}
