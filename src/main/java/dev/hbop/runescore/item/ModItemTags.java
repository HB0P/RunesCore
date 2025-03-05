package dev.hbop.runescore.item;

import dev.hbop.runescore.RunesCore;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ModItemTags {
    public static final TagKey<Item> ANVIL_REPAIR_MATERIALS = TagKey.of(RegistryKeys.ITEM, RunesCore.identifier("anvil_repair_materials"));

}