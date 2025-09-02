package dev.hbop.runescore;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

public class ModItems {

    public static Item RUNE = Items.register(
            RegistryKey.of(RegistryKeys.ITEM, RunesCore.identifier("rune")),
            Item::new,
            new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)
    );

    public static void initialiseItems() {

    }
}