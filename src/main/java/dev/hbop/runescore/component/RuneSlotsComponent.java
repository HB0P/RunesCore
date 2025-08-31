package dev.hbop.runescore.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Consumer;

public record RuneSlotsComponent(
        Map<Identifier, Integer> usedSlots, 
        int maxSlots
) implements TooltipAppender {

    public static final Codec<RuneSlotsComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.unboundedMap(Identifier.CODEC, Codec.INT).fieldOf("used_slots").forGetter(RuneSlotsComponent::usedSlots),
                    Codec.INT.fieldOf("max_slots").forGetter(RuneSlotsComponent::maxSlots)
            ).apply(instance, RuneSlotsComponent::new)
    );

    public int countUsedSlots() {
        int total = 0;
        for (int c : usedSlots.values()) {
            total += c;
        }
        return total;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type, ComponentsAccess components) {
        tooltip.accept(
                Text.translatable("item.rune.runes").formatted(Formatting.GRAY).append(
                        Text.literal(countUsedSlots() + "/" + maxSlots).formatted(Formatting.BLUE)
                )
        );
    }
}
