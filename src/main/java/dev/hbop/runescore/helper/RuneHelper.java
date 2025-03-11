package dev.hbop.runescore.helper;

import dev.hbop.runescore.component.RuneComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

public class RuneHelper {
    
    public static int getMaxRuneSlots(ItemStack stack) {
        return 20;
    }
    
    public static List<RegistryEntry<Enchantment>> getEnchantmentsFor(ItemStack item, RuneComponent runeComponent) {
        if (runeComponent == null) return null;
        for (TagKey<Item> tag : runeComponent.enchantments().keySet()) {
            if (item.streamTags().anyMatch(t -> t == tag)) {
                return runeComponent.enchantments().get(tag);
            }
        }
        return null;
    }
}

