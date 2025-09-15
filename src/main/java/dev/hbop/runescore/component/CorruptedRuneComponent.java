package dev.hbop.runescore.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.registry.RuneTemplate;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public record CorruptedRuneComponent(List<Identifier> targetRunes, List<RegistryEntry<Enchantment>> targetEnchantments) implements AbstractRuneComponent {

    public static final Codec<Either<CorruptedRuneComponent, RegistryEntry<RuneTemplate>>> EITHER_CODEC = Codec.either(
            RecordCodecBuilder.create(
                    instance -> instance.group(
                            Codecs.listOrSingle(Identifier.CODEC).optionalFieldOf("target_runes", List.of()).forGetter(CorruptedRuneComponent::targetRunes), 
                            Codecs.listOrSingle(Enchantment.ENTRY_CODEC).optionalFieldOf("target_enchantments", List.of()).forGetter(CorruptedRuneComponent::targetEnchantments)
                    ).apply(instance, CorruptedRuneComponent::new)
            ),
            RuneTemplate.ENTRY_CODEC
    );
    public static final Codec<CorruptedRuneComponent> CODEC = EITHER_CODEC.xmap(
            either -> either.map(
                    Function.identity(),
                    RuneTemplate::toCorruptedRuneComponent
            ),
            Either::left
    );

    @Override
    public @Nullable AppliedRunesComponent apply(AppliedRunesComponent appliedRunesComponent, Predicate<TagKey<Item>> predicate) {
        Map<Identifier, AppliedRunesComponent.AppliedRune> appliedRunes = new HashMap<>();
        for (Map.Entry<Identifier, AppliedRunesComponent.AppliedRune> entry : appliedRunesComponent.runes().entrySet()) {
            if (targetRunes.contains(entry.getKey())) continue;
            if (Collections.disjoint(entry.getValue().enchantments().keySet(), targetEnchantments)) {
                appliedRunes.put(entry.getKey(), entry.getValue());
            }
        }
        if (appliedRunes.size() == appliedRunesComponent.runes().size()) return null;
        return new AppliedRunesComponent(appliedRunes);
    }

    @Override
    public CorruptedRuneComponent corrupt() {
        return new CorruptedRuneComponent(targetRunes, targetEnchantments);
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type, ComponentsAccess components) {
        if (!targetRunes.isEmpty()) {
            tooltip.accept(Text.translatable("item.rune.removes").formatted(Formatting.RED));
            for (Identifier id : targetRunes) {
                tooltip.accept(ScreenTexts.space().append(Text.translatable("item." + id.getNamespace() + ".rune." + id.getPath()).formatted(Formatting.GRAY)));
            }
        }
        if (!targetEnchantments.isEmpty()) {
            tooltip.accept(Text.translatable("item.rune.removes_enchantments").formatted(Formatting.RED));
            for (RegistryEntry<Enchantment> enchantment : targetEnchantments) {
                tooltip.accept(ScreenTexts.space().append(enchantment.value().description()).formatted(Formatting.GRAY));
            }
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int applicationPriority() {
        return 0;
    }

    @Override
    public int corruptingPriority() {
        return 1;
    }
}