package dev.hbop.runescore.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.RunesCore;
import dev.hbop.runescore.component.CorruptedRuneComponent;
import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Map;

public record RuneTemplate(int size, Map<TagKey<Item>, Map<RegistryEntry<Enchantment>, Integer>> enchantments, List<Identifier> removesRunes, List<RegistryEntry<Enchantment>> removesEnchantments) {
    
    public static final RegistryKey<Registry<RuneTemplate>> RUNE_REGISTRY = RegistryKey.ofRegistry(RunesCore.identifier("rune"));
    
    public static final Codec<RuneTemplate> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codecs.NON_NEGATIVE_INT.optionalFieldOf("size", 0).forGetter(RuneTemplate::size),
                    Codecs.nonEmptyMap(Codec.unboundedMap(TagKey.codec(RegistryKeys.ITEM), Codecs.nonEmptyMap(Codec.unboundedMap(Enchantment.ENTRY_CODEC, Codec.intRange(1, 255))))).fieldOf("enchantments").forGetter(RuneTemplate::enchantments),
                    Codecs.listOrSingle(Identifier.CODEC).optionalFieldOf("removes_runes", List.of()).forGetter(RuneTemplate::removesRunes),
                    Codecs.listOrSingle(Enchantment.ENTRY_CODEC).optionalFieldOf("removes_enchantments", List.of()).forGetter(RuneTemplate::removesEnchantments)
            ).apply(instance, RuneTemplate::new)
    );
    public static final Codec<RegistryEntry<RuneTemplate>> ENTRY_CODEC = RegistryFixedCodec.of(RUNE_REGISTRY);
    
    private RuneComponent toRuneComponent(Identifier id) {
        return new RuneComponent(id, this.size, this.enchantments);
    }
    
    public static RuneComponent toRuneComponent(RegistryEntry<RuneTemplate> rune) {
        return rune.value().toRuneComponent(rune.getKey().orElseThrow().getValue());
    }
    
    private CorruptedRuneComponent toCorruptedRuneComponent() {
        if (removesRunes.isEmpty() && removesEnchantments.isEmpty()) return null;
        return new CorruptedRuneComponent(removesRunes, removesEnchantments);
    }

    public static CorruptedRuneComponent toCorruptedRuneComponent(RegistryEntry<RuneTemplate> rune) {
        return rune.value().toCorruptedRuneComponent();
    }
    
    private ComponentMap getComponents(Identifier id) {
        ComponentMap.Builder builder = ComponentMap.builder();
        builder.add(ModComponents.RUNE_COMPONENT, toRuneComponent(id));
        builder.add(ModComponents.CORRUPTED_RUNE_COMPONENT, toCorruptedRuneComponent());
        builder.add(DataComponentTypes.ITEM_NAME, Text.translatable("item." + id.getNamespace() + ".rune." + id.getPath()));
        builder.add(DataComponentTypes.ITEM_MODEL, id.withPath("rune/" + id.getPath()));
        return builder.build();
    }
    
    public static ComponentMap getComponents(RegistryEntry<RuneTemplate> rune) {
        return rune.value().getComponents(rune.getKey().orElseThrow().getValue());
    }
    
    public static void register() {
        DynamicRegistries.registerSynced(RUNE_REGISTRY, CODEC);
    }
}