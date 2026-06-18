package dev.d4vid.mods.genesis.server

import dev.d4vid.mods.genesis.server.cooldown.CooldownType
import kotlinx.serialization.json.*
import java.time.Duration
import java.util.*

object GenesisConfig {
    private var disableNether = true
    private var disableEnd = true

    private var cooldowns = EnumMap<CooldownType, Duration>(CooldownType::class.java)

    fun load(raw: String) {
        val json = Json.parseToJsonElement(raw).jsonObject

        disableNether = json.getValue("disableNether").jsonPrimitive.boolean
        disableEnd = json.getValue("disableEnd").jsonPrimitive.boolean

        cooldowns.clear()
        json.getValue("cooldowns").jsonObject.entries.forEach { (key, valueElement) ->
            val enum = CooldownType.fromKey(key)
            if (enum == null) {
                Genesis.logger.warn("Unknown cooldown key $key!")
                return@forEach
            }

            val value = valueElement.jsonPrimitive.double
            if (value < 0) {
                Genesis.logger.warn("Cooldown for $key cannot be negative!")
                return@forEach
            }

            cooldowns[enum] = Duration.ofMillis((value * 1000).toLong())
        }
    }

    fun isNetherDisabled(): Boolean {
        return disableNether
    }

    fun isEndDisabled(): Boolean {
        return disableEnd
    }

    fun getCooldownDuration(type: CooldownType): Duration {
        return cooldowns[type] ?: Duration.ofMillis(0)
    }
}
