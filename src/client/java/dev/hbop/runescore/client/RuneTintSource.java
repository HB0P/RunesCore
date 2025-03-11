package dev.hbop.runescore.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record RuneTintSource(List<Integer> colors, int defaultColor) implements TintSource {
    
    public static final MapCodec<RuneTintSource> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.list(Codecs.RGB).fieldOf("colors").forGetter(RuneTintSource::colors),
                    Codecs.RGB.optionalFieldOf("default", 0).forGetter(RuneTintSource::defaultColor)
            ).apply(instance, RuneTintSource::new)
    );
    
    @Override
    public MapCodec<RuneTintSource> getCodec() {
        return CODEC;
    }

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        RuneComponent component = stack.get(ModComponents.RUNE_COMPONENT);
        if (component == null) return defaultColor;
        if (component.level() >= colors.size()) return defaultColor;
        return colors.get(component.level());
    }
}