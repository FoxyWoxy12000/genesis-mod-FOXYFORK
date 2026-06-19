package dev.d4vid.mods.genesis.server.mixin.resourcePack;

import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ResourcePackPlayerData {

    public Set<UUID> acceptedPlayers = new HashSet<>();
    private static ResourcePackPlayerData INSTANCE;
    private static File dataFile;

    public static ResourcePackPlayerData get(MinecraftServer server) {
        if (INSTANCE == null) {
            INSTANCE = new ResourcePackPlayerData();
        }
        return INSTANCE;
    }

    public static void load(MinecraftServer server) {
        dataFile = new File(server.getServerDirectory().toFile(), "genesis_pack_accepted.txt");
        if (INSTANCE == null) INSTANCE = new ResourcePackPlayerData();
        if (!dataFile.exists()) return;
        try {
            String content = Files.readString(dataFile.toPath());
            for (String line : content.split("\n")) {
                if (!line.isBlank()) {
                    INSTANCE.acceptedPlayers.add(UUID.fromString(line.trim()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            StringBuilder sb = new StringBuilder();
            for (UUID uuid : acceptedPlayers) {
                sb.append(uuid.toString()).append("\n");
            }
            Files.writeString(dataFile.toPath(), sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
