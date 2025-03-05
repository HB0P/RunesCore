package dev.hbop.runescore.item;

import dev.hbop.runescore.RunesCore;
import dev.hbop.runescore.helper.RuneHelper;
import dev.hbop.runescore.helper.RuneInfo;
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
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> {
            for (RuneInfo info : RuneHelper.RUNE_INFOS) {
                for (int level = 0; level <= info.getMaxLevel(); level++) {
                    int l = level;
                    ItemStack stack = new ItemStack(RUNE);
                    itemGroup.getContext().lookup().getOptional(RegistryKeys.ENCHANTMENT).ifPresent((registryWrapper) ->
                            stack.applyComponentsFrom(info.getComponents(l, registryWrapper))
                    );
                    itemGroup.add(stack);
                }
            }
        });
    }
}