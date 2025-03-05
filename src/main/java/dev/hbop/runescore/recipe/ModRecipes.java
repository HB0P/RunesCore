package dev.hbop.runescore.recipe;

import dev.hbop.runescore.RunesCore;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipes {
    
    public static RecipeSerializer<RuneCorruptingRecipe> RUNE_CORRUPTING_RECIPE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            RunesCore.identifier("crafting_special_runecorrupting"),
            new SpecialCraftingRecipe.SpecialRecipeSerializer<>(RuneCorruptingRecipe::new)
    );
    
    public static void registerRecipes() {
        
    }
}