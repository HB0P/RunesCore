package dev.hbop.runescore.helper;

import dev.hbop.runescore.component.RuneComponent;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RuneHelper {

    public static List<RuneTemplate> RUNE_TEMPLATES = new ArrayList<>();
    
    @Nullable
    public static RuneTemplate getRuneInfo(Identifier id) {
        for (RuneTemplate info : RUNE_TEMPLATES) {
            if (info.identifier().equals(id)) {
                return info;
            }
        }
        return null;
    }
    
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
    
    public static void registerReloadListener() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ReloadListener());
    }
}

