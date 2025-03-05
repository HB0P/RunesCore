package dev.hbop.runescore.helper;

import dev.hbop.runescore.RunesCore;
import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import net.minecraft.component.ComponentMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

public class RuneInfo {
    
    private final Identifier identifier;
    private final int maxLevel;
    private final int baseSlots;
    private final int bonusSlots;
    private final Map<Supplier<TagKey<Item>>, Function<RegistryWrapper.Impl<Enchantment>, List<RegistryEntry<Enchantment>>>> enchantments;

    public RuneInfo(Identifier identifier, int maxLevel, int baseSlots, int bonusSlots, Map<Supplier<TagKey<Item>>, Function<RegistryWrapper.Impl<Enchantment>, List<RegistryEntry<Enchantment>>>> enchantments) {
        this.identifier = identifier;
        this.maxLevel = maxLevel;
        this.baseSlots = baseSlots;
        this.bonusSlots = bonusSlots;
        this.enchantments = enchantments;
    }

    public Identifier getIdentifier() {
        return identifier;
    }
    
    public int getMaxLevel() {
        return maxLevel;
    }
    
    public ComponentMap getComponents(int level, RegistryWrapper.Impl<Enchantment> registryWrapper) {
        Map<TagKey<Item>, List<RegistryEntry<Enchantment>>> map = new HashMap<>();
        for (Supplier<TagKey<Item>> tag : enchantments.keySet()) {
            try {
                map.put(tag.get(), enchantments.get(tag).apply(registryWrapper));
            } catch (NoSuchElementException e) {
                RunesCore.LOGGER.warn("Tried to create rune [" + identifier + "] with invalid enchantment map - will be empty");
            }
        }
        return ComponentMap.builder().add(
                ModComponents.RUNE_COMPONENT,
                new RuneComponent(identifier, level, level == 0 ? 0 : baseSlots + level * bonusSlots, map)
        ).build();
    }
}
