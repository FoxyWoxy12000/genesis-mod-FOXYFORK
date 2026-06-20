package dev.d4vid.mods.genesis.server.combat

import kotlinx.io.files.Path
import net.minecraft.world.entity.player.Player
import java.util.UUID
import java.io.File;

object PvpProtectionData {
    const val PATH = "genesis_pvp_protection.txt"
    private val protectedPlayers = mutableMapOf<UUID, Long>()
    private var dataFile = File(PATH)

    fun load() {
        dataFile = File(PATH)
        if (!dataFile.exists()) return
        try {
            for (line in dataFile.readLines()) {
                if (line.isEmpty()) continue
                val parts = line.split(",")
                if (parts.size != 2) continue
                val uuid = UUID.fromString(parts[0].trim())
                val expires = parts[1].trim().toLong()
                protectedPlayers[uuid] = expires
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun save() {
        try {
            val writer = dataFile.writer()
            for ((uuid, expires) in protectedPlayers) {
                writer.appendLine("$uuid : $expires")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun grantProtection(uuid: UUID) {
        protectedPlayers[uuid] = System.currentTimeMillis() + (60 * 60 * 1000L)
        save()
    }

    fun isProtected(uuid: UUID): Boolean {
        val expires = protectedPlayers[uuid] ?: return false
        if (System.currentTimeMillis() > expires) {
            protectedPlayers.remove(uuid)
            return false
        }
        return true
    }

    fun getTimeLeft(uuid: UUID): Long {
        val expires = protectedPlayers[uuid] ?: return 0L
        return maxOf(0L, expires - System.currentTimeMillis())
    }
}
