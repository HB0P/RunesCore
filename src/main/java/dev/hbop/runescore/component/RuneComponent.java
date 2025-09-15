package dev.hbop.runescore.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.registry.RuneTemplate;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public record RuneComponent(Identifier id, int size, Map<TagKey<Item>, Map<RegistryEntry<Enchantment>, Integer>> enchantments) implements AbstractRuneComponent {

    private static final Codec<Either<RuneComponent, RegistryEntry<RuneTemplate>>> EITHER_CODEC = Codec.either(
            RecordCodecBuilder.create(
                    instance -> instance.group(
                            Identifier.CODEC.fieldOf("id").forGetter(RuneComponent::id),
                            Codecs.NON_NEGATIVE_INT.optionalFieldOf("size", 0).forGetter(RuneComponent::size),
                            Codecs.nonEmptyMap(Codec.unboundedMap(TagKey.codec(RegistryKeys.ITEM), Codecs.nonEmptyMap(Codec.unboundedMap(Enchantment.ENTRY_CODEC, Codec.intRange(1, 255))))).fieldOf("enchantments").forGetter(RuneComponent::enchantments)
                    ).apply(instance, RuneComponent::new)
            ),
            RuneTemplate.ENTRY_CODEC
    );
    public static final Codec<RuneComponent> CODEC = EITHER_CODEC.xmap(
            either -> either.map(
                    Function.identity(),
                    RuneTemplate::toRuneComponent
            ),
            Either::left
    );

    @Override
    public @Nullable AppliedRunesComponent apply(AppliedRunesComponent appliedRunesComponent, Predicate<TagKey<Item>> predicate) {
        Map<Identifier, AppliedRunesComponent.AppliedRune> appliedRunes = new HashMap<>(appliedRunesComponent.runes());
        if (appliedRunes.containsKey(id)) {
            return null;
        }

        Map<RegistryEntry<Enchantment>, Integer> appliedEnchantments = new HashMap<>();
        for (Map.Entry<TagKey<Item>, Map<RegistryEntry<Enchantment>, Integer>> entry : enchantments.entrySet()) {
            if (predicate.test(entry.getKey())) {
                appliedEnchantments.putAll(entry.getValue());
            }
        }
        if (appliedEnchantments.isEmpty()) {
            return null;
        }
        AppliedRunesComponent.AppliedRune appliedRune = new AppliedRunesComponent.AppliedRune(size, appliedEnchantments);

        appliedRunes.put(id, appliedRune);
        return new AppliedRunesComponent(appliedRunes);
    }

    @Override
    public CorruptedRuneComponent corrupt() {
        return new CorruptedRuneComponent(List.of(id), List.of());
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type, ComponentsAccess components) {
        if (size != 0) {
            tooltip.accept(Text.translatable("item.rune.size", Text.literal("" + size).formatted(Formatting.BLUE)).formatted(Formatting.GRAY));
            tooltip.accept(Text.empty());
        }
        
        for (TagKey<Item> tag : enchantments.keySet()) {
            tooltip.accept(Text.translatable("item.rune.when_applied_to", Text.empty().append(tag.getName()).formatted(Formatting.GREEN)).formatted(Formatting.GRAY));
            for (Map.Entry<RegistryEntry<Enchantment>, Integer> enchantment : enchantments.get(tag).entrySet()) {
                tooltip.accept(ScreenTexts.space().append(Enchantment.getName(enchantment.getKey(), enchantment.getValue())));
            }
        }
    }

    @Override
    public int applicationPriority() {
        return 1;
    }

    @Override
    public int corruptingPriority() {
        return 0;
    }
}
