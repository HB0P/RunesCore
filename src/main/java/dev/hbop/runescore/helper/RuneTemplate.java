package dev.hbop.runescore.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.RunesCore;
import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class RuneTemplate {

    public static final Codec<RuneTemplate> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.POSITIVE_INT.fieldOf("max_level").forGetter(t -> t.maxLevel),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("base_size", 0).forGetter(t -> t.baseSize),
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("bonus_size", 0).forGetter(t -> t.bonusSize),
                    Codec.unboundedMap(TagKey.codec(RegistryKeys.ITEM), Codecs.listOrSingle(Enchantment.ENTRY_CODEC)).fieldOf("enchantments").forGetter(t -> t.enchantments),
                    Identifier.CODEC.optionalFieldOf("item_model").forGetter(t -> t.itemModel)
            ).apply(instance, RuneTemplate::new)
    );
    public static final Codec<RegistryEntry<RuneTemplate>> ENTRY_CODEC = RegistryFixedCodec.of(RunesCore.RUNE_TEMPLATE_REGISTRY);
    
    private final int maxLevel;
    private final int baseSize;
    private final int bonusSize;
    private final Map<TagKey<Item>, List<RegistryEntry<Enchantment>>> enchantments;
    private final Optional<Identifier> itemModel;

    public RuneTemplate(int maxLevel, int baseSize, int bonusSize, Map<TagKey<Item>, List<RegistryEntry<Enchantment>>> enchantments, Optional<Identifier> itemModel) {
        this.maxLevel = maxLevel;
        this.baseSize = baseSize;
        this.bonusSize = bonusSize;
        this.enchantments = enchantments;
        this.itemModel = itemModel;
    }

    public ComponentMap getComponents(Identifier identifier, int level) {
        ComponentMap.Builder builder = ComponentMap.builder();
        builder.add(
                ModComponents.RUNE_COMPONENT,
                new RuneComponent(identifier, level, level == 0 ? 0 : baseSize + level * bonusSize, enchantments)
        );
        itemModel.ifPresent(value -> builder.add(DataComponentTypes.ITEM_MODEL, value));
        return builder.build();
    }

    public void forEachLevel(Consumer<Integer> consumer) {
        for (int level = 0; level <= maxLevel; level++) {
            consumer.accept(level);
        }
    }
}
