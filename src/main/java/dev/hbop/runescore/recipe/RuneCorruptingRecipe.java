package dev.hbop.runescore.recipe;

import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class RuneCorruptingRecipe extends SpecialCraftingRecipe {
    
    public RuneCorruptingRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        if (input.getWidth() == 3 && input.getHeight() == 3 && input.getStackCount() == 9) {
            for (int i = 0; i < input.getHeight(); i++) {
                for (int j = 0; j < input.getWidth(); j++) {
                    ItemStack itemStack = input.getStackInSlot(j, i);
                    if (itemStack.isEmpty()) return false;
                    if (j == 1 && i == 1) {
                        RuneComponent component = itemStack.get(ModComponents.RUNE_COMPONENT);
                        if (component == null) return false;
                    } else if (!itemStack.isOf(Items.NETHER_WART)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        ItemStack rune = input.getStackInSlot(1, 1);
        RuneComponent runeComponent = rune.get(ModComponents.RUNE_COMPONENT);
        assert runeComponent != null;
        
        ItemStack output = rune.copy();
        output.set(ModComponents.RUNE_COMPONENT, new RuneComponent(
                runeComponent.identifier(),
                0, 0,
                runeComponent.enchantments()
        ));
        return output;
    }
    
    @Override
    public DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input) {
        DefaultedList<ItemStack> list = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);
        list.set(4, input.getStackInSlot(1, 1).copy());
        return list;
    }

    @Override
    public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
        return ModRecipes.RUNE_CORRUPTING_RECIPE_SERIALIZER;
    }
}