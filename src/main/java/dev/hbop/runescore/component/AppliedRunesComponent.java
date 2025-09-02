package dev.hbop.runescore.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public record AppliedRunesComponent(Map<Identifier, AppliedRune> runes) implements TooltipAppender {

    public static final Codec<AppliedRunesComponent> CODEC = Codecs.nonEmptyMap(Codec.unboundedMap(Identifier.CODEC, AppliedRune.CODEC)).xmap(
            AppliedRunesComponent::new,
            AppliedRunesComponent::runes
    );
    
    public int getTotalSize() {
        int totalSize = 0;
        for (AppliedRune rune : runes.values()) {
            totalSize += rune.size;
        }
        return totalSize;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type, ComponentsAccess components) {
        Integer capacity = components.get(ModComponents.RUNE_CAPACITY_COMPONENT);
        int totalSize = getTotalSize();
        
        String runeUsageString = capacity == null ? "" + totalSize : totalSize + "/" + capacity;
        tooltip.accept(Text.translatable("item.rune.runes", Text.literal(runeUsageString).formatted(Formatting.BLUE)).formatted(Formatting.GRAY));
        tooltip.accept(Text.empty());

        ItemEnchantmentsComponent enchantmentsComponent = components.get(DataComponentTypes.ENCHANTMENTS);
        assert enchantmentsComponent != null;
        Set<RegistryEntry<Enchantment>> enchantments = new HashSet<>(enchantmentsComponent.getEnchantments());
        
        for (Map.Entry<Identifier, AppliedRune> entry : runes.entrySet()) {
            tooltip.accept(Text.translatable("item." + entry.getKey().getNamespace() + ".rune." + entry.getKey().getPath()).formatted(Formatting.BLUE).append(Text.literal(" (" + entry.getValue().size + ")").formatted(Formatting.GRAY)));
            for (Map.Entry<RegistryEntry<Enchantment>, Integer> enchantment : entry.getValue().enchantments.entrySet()) {
                Formatting formatting;
                if (enchantments.contains(enchantment.getKey()) && enchantmentsComponent.getLevel(enchantment.getKey()) == enchantment.getValue()) {
                    enchantments.remove(enchantment.getKey());
                    formatting = Formatting.WHITE;
                }
                else {
                    formatting = Formatting.GRAY;
                }
                tooltip.accept(ScreenTexts.space().append(Enchantment.getName(enchantment.getKey(), enchantment.getValue()).copy().formatted(formatting)));
            }
        }
    }
    
    public record AppliedRune(int size, Map<RegistryEntry<Enchantment>, Integer> enchantments) {

        public static final Codec<AppliedRune> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codecs.NON_NEGATIVE_INT.optionalFieldOf("size", 0).forGetter(AppliedRune::size),
                        Codecs.nonEmptyMap(Codec.unboundedMap(Enchantment.ENTRY_CODEC, Codec.intRange(1, 255))).fieldOf("enchantments").forGetter(AppliedRune::enchantments)
                ).apply(instance, AppliedRune::new)
        );
    }
}