package dev.hbop.runescore.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.RunesCore;
import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import net.minecraft.component.ComponentMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.dynamic.Codecs;

import java.util.*;

public record RuneTemplate(Identifier identifier, int maxLevel, int baseSize, int bonusSize, Map<TagKey<Item>, List<EnchantmentSupplier>> enchantments, int priority) {

    public static final Codec<RuneTemplate> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(RuneTemplate::identifier),
                    Codecs.POSITIVE_INT.fieldOf("max_level").forGetter(RuneTemplate::maxLevel),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("base_size", 0).forGetter(RuneTemplate::baseSize),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("bonus_size", 0).forGetter(RuneTemplate::bonusSize),
                    Codec.unboundedMap(TagKey.codec(RegistryKeys.ITEM), Codecs.listOrSingle(EnchantmentSupplier.CODEC)).fieldOf("enchantments").forGetter(RuneTemplate::enchantments),
                    Codec.INT.optionalFieldOf("priority", 0).forGetter(RuneTemplate::priority)
            ).apply(instance, RuneTemplate::new)
    );

    public ComponentMap getComponents(int level, RegistryWrapper.Impl<Enchantment> registryWrapper) {
        Map<TagKey<Item>, List<RegistryEntry<Enchantment>>> map = new HashMap<>();
        for (TagKey<Item> tagKey : enchantments.keySet()) {
            List<RegistryEntry<Enchantment>> list = new ArrayList<>();
            for (EnchantmentSupplier supplier : enchantments.get(tagKey)) {
                Optional<RegistryEntry.Reference<Enchantment>> optionalEnchantment = supplier.getEnchantment(registryWrapper);
                if (optionalEnchantment.isPresent()) {
                    list.add(optionalEnchantment.get());
                }
                else {
                    RunesCore.LOGGER.warn("Tried to create rune with invalid enchantment '" + supplier.enchantmentID + "' - will be skipped");
                }
            }
            if (!list.isEmpty()) map.put(tagKey, list);
        }
        return ComponentMap.builder().add(
                ModComponents.RUNE_COMPONENT,
                new RuneComponent(identifier, level, level == 0 ? 0 : baseSize + level * bonusSize, map)
        ).build();
    }
    
    private record EnchantmentSupplier(Identifier enchantmentID) {
        
        private static final Codec<EnchantmentSupplier> CODEC = Codec.STRING.comapFlatMap(EnchantmentSupplier::validate, EnchantmentSupplier::toString).stable();
        
        private Optional<RegistryEntry.Reference<Enchantment>> getEnchantment(RegistryWrapper.Impl<Enchantment> registryWrapper) {
            return registryWrapper.streamEntries().filter((reference) -> reference.matchesId(enchantmentID)).findAny();
        }

        public static DataResult<EnchantmentSupplier> validate(String id) {
            try {
                return DataResult.success(new EnchantmentSupplier(Identifier.of(id)));
            } catch (InvalidIdentifierException var2) {
                return DataResult.error(() -> "Not a valid resource location: " + id + " " + var2.getMessage());
            }
        }
        
        @Override
        public String toString() {
            return enchantmentID.toString();
        }
    }
}
