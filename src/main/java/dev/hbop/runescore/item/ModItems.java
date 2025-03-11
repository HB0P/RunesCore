package dev.hbop.runescore.item;

import dev.hbop.runescore.RunesCore;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

public class ModItems {

    public static Item RUNE = Items.register(
            RegistryKey.of(RegistryKeys.ITEM, RunesCore.identifier("rune")),
            RuneItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)
    );

    public static void initialiseItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> 
                itemGroup.getContext().lookup().getOptional(RunesCore.RUNE_TEMPLATE_REGISTRY).ifPresent((templateRegistry) -> 
                        templateRegistry.streamEntries().forEach(reference -> 
                                reference.value().forEachLevel(level -> {
                                    ItemStack stack = new ItemStack(RUNE);
                                    stack.applyComponentsFrom(reference.value().getComponents(reference.getKey().orElseThrow().getValue(), level));
                                    itemGroup.add(stack);
                                })
                        )
                )
        );
    }
}