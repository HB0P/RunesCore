package dev.hbop.runescore;

import dev.hbop.runescore.component.AbstractRuneComponent;
import dev.hbop.runescore.component.ModComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;

public class RuneCorruptingRecipe extends SpecialCraftingRecipe {

    public static RecipeSerializer<RuneCorruptingRecipe> SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            RunesCore.identifier("crafting_special_runecorrupting"),
            new SpecialCraftingRecipe.SpecialRecipeSerializer<>(RuneCorruptingRecipe::new)
    );
    
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
                        List<AbstractRuneComponent> components = ModComponents.getAbstractRuneComponents(itemStack);
                        if (components.isEmpty()) return false;
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
        List<AbstractRuneComponent> components = ModComponents.getAbstractRuneComponents(rune);
        components.sort(Comparator.comparingInt(AbstractRuneComponent::corruptingPriority));
        AbstractRuneComponent component = components.getFirst();
        
        ItemStack output = new ItemStack(ModItems.RUNE);
        output.set(ModComponents.CORRUPTED_RUNE_COMPONENT, component.corrupt());
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
        return SERIALIZER;
    }
    
    public static void register() {}
}