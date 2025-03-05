package dev.hbop.runescore.recipe;

import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import dev.hbop.runescore.helper.RuneHelper;
import dev.hbop.runescore.helper.RuneInfo;
import dev.hbop.runescore.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKeys;
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
                        if (!itemStack.isOf(ModItems.RUNE)) return false;
                        RuneComponent component = itemStack.get(ModComponents.RUNE_COMPONENT);
                        if (component == null) return false;
                        if (RuneHelper.getRuneInfo(component.identifier()) == null) return false;
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
        RuneComponent runeComponent = input.getStackInSlot(1, 1).get(ModComponents.RUNE_COMPONENT);
        assert runeComponent != null;
        RuneInfo info = RuneHelper.getRuneInfo(runeComponent.identifier());
        assert info != null;
        ItemStack output = new ItemStack(ModItems.RUNE);
        output.applyComponentsFrom(
                info.getComponents(0, registries.getOrThrow(RegistryKeys.ENCHANTMENT))
        );
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