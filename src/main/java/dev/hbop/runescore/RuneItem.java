package dev.hbop.runescore;

import dev.hbop.runescore.registry.RuneTemplate;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

public class RuneItem {
    
    public static Item RUNE = Items.register(
            RegistryKey.of(RegistryKeys.ITEM, RunesCore.identifier("rune")),
            Item::new,
            new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)
    );
    
    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(itemGroup ->
                itemGroup.getContext().lookup().getOptional(RuneTemplate.RUNE_REGISTRY).ifPresent(templateRegistry ->
                        templateRegistry.streamEntries().forEach(reference -> {
                            ItemStack stack = new ItemStack(RUNE);
                            stack.applyComponentsFrom(RuneTemplate.getComponents(reference));
                            itemGroup.add(stack);
                        })
                )
        );
    }
}