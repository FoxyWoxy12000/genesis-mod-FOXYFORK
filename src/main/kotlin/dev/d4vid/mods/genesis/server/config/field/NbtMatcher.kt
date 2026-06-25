package dev.d4vid.mods.genesis.server.config.field

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import dev.d4vid.mods.genesis.server.config.serialization.IdentifierSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

@Serializable
data class NbtMatcher(
    @Serializable(with = IdentifierSerializer::class)
    val identifier: Identifier,
    val nbt: JsonElement? = null,
) {
    @Transient
    val nbtGson = nbt?.let { JsonParser.parseString(it.toString()) }

    @Transient
    private val blockTagKey = TagKey.create(Registries.BLOCK, identifier)

    @Transient
    private val itemTagKey = TagKey.create(Registries.ITEM, identifier)

    fun matchBlock(state: BlockState): Boolean {
        if (BuiltInRegistries.BLOCK.getKey(state.block) != identifier) {
            if (!state.`is`(blockTagKey)) {
                return false
            }
        }

        return true
    }

    fun matchItem(stack: ItemStack): Boolean {
        if (BuiltInRegistries.ITEM.getKey(stack.item) != identifier) {
            if (!stack.`is`(itemTagKey)) {
                return false
            }
        }

        return nbtGson == null || matchNbt(nbtGson, stack.components)
    }

    private fun matchNbt(json: com.google.gson.JsonElement, components: DataComponentMap): Boolean {
        val nbtJson = DataComponentMap.CODEC.encodeStart(JsonOps.INSTANCE, components).getOrThrow()

        return matchJson(json, nbtJson)
    }

    private fun matchJson(a: com.google.gson.JsonElement, b: com.google.gson.JsonElement): Boolean {
        if (a.isJsonObject) {
            if (!b.isJsonObject) {
                return false
            }

            val aObj = a.getAsJsonObject()
            val bObj = b.getAsJsonObject()

            for ((key, value) in aObj.entrySet()) {
                if (!bObj.has(key) || !matchJson(value, bObj.get(key))) {
                    return false
                }
            }

            return true
        }

        if (a.isJsonArray) {
            if (!b.isJsonArray) {
                return false
            }

            val aArr = a.getAsJsonArray()
            val bArr = b.getAsJsonArray()

            if (aArr.size() != bArr.size()) {
                return false
            }

            for (i in 0..<aArr.size()) {
                if (!matchJson(aArr.get(i), bArr.get(i))) {
                    return false
                }
            }

            return true
        }

        return a == b
    }
}
