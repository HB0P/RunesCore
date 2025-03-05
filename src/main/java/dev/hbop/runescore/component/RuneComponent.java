package dev.hbop.runescore.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record RuneComponent(
        Identifier identifier,
        int level,
        int size,
        Map<TagKey<Item>, List<RegistryEntry<Enchantment>>> enchantments
) implements TooltipAppender {

    public static final Codec<RuneComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Identifier.CODEC.fieldOf("identifier").forGetter(RuneComponent::identifier),
                    Codec.INT.fieldOf("level").forGetter(RuneComponent::level),
                    Codec.INT.optionalFieldOf("size", 0).forGetter(RuneComponent::size),
                    Codec.unboundedMap(TagKey.codec(RegistryKeys.ITEM), Codecs.listOrSingle(Enchantment.ENTRY_CODEC)).optionalFieldOf("enchantments", Map.of()).forGetter(RuneComponent::enchantments)
            ).apply(instance, RuneComponent::new)
    );

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (level != 0) tooltip.accept(Text.translatable("item.rune.level", Text.translatable("enchantment.level." + level).formatted(Formatting.BLUE)).formatted(Formatting.GRAY));
        if (size != 0) tooltip.accept(Text.translatable("item.rune.size", Text.literal("" + size).formatted(Formatting.BLUE)).formatted(Formatting.GRAY));
        if (!enchantments.isEmpty()) {
            if (level != 0 || size != 0) tooltip.accept(Text.empty());
            for (TagKey<Item> tag : enchantments.keySet()) {
                tooltip.accept(Text.translatable("item.rune.when_applied_to", Text.empty().append(tag.getName()).formatted(Formatting.BLUE)).formatted(Formatting.GRAY));
                for (RegistryEntry<Enchantment> enchantment : enchantments.get(tag)) {
                    if (level == 0) {
                        tooltip.accept(ScreenTexts.space().append(Text.translatable("item.rune.removes", enchantment.value().description()).formatted(Formatting.RED)));
                    }
                    else {
                        tooltip.accept(ScreenTexts.space().append(enchantment.value().description()).formatted(Formatting.GREEN));
                    }
                }
            }
        }
    }
}
