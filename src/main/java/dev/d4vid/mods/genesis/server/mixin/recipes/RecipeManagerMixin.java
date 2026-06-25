package dev.d4vid.mods.genesis.server.mixin.recipes;

import dev.d4vid.mods.genesis.server.event.GenesisRecipeEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@SuppressWarnings("unused")
@Mixin(RecipeManager.class)
class RecipeManagerMixin {
    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeHolder;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    private <I extends RecipeInput, T extends Recipe<I>> void genesis$getRecipeFor(RecipeType<T> recipeType, I recipeInput, Level level, @Nullable RecipeHolder<T> recipeHolder, CallbackInfoReturnable<Optional<RecipeHolder<T>>> callback) {
        if (recipeHolder == null) {
            return;
        }

        handle(recipeHolder, recipeInput, level, callback);
    }

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true)
    private <I extends RecipeInput, T extends Recipe<I>> void genesis$getRecipeFor(RecipeType<T> recipeType, I recipeInput, Level level, CallbackInfoReturnable<Optional<RecipeHolder<T>>> callback) {
        callback.getReturnValue().ifPresent((recipeHolder) -> handle(recipeHolder, recipeInput, level, callback));
    }

    private <I extends RecipeInput, T extends Recipe<I>> void handle(RecipeHolder<T> recipeHolder, I recipeInput, Level level, CallbackInfoReturnable<Optional<RecipeHolder<T>>> callback) {
        T recipe = recipeHolder.value();
        ItemStack stack = recipe.assemble(recipeInput, level.registryAccess());
        ItemStack[] input = new ItemStack[recipeInput.size()];

        for (int i = 0; i < recipeInput.size(); i++) {
            input[i] = recipeInput.getItem(i);
        }

        boolean result = GenesisRecipeEvents.INSTANCE.getALLOW().invoker().allow(input, stack);

        if (!result) {
            callback.setReturnValue(Optional.empty());
        }
    }
}
