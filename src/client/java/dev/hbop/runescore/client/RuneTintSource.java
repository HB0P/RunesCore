package dev.hbop.runescore.client;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import dev.hbop.runescore.helper.RuneHelper;
import dev.hbop.runescore.helper.RuneTemplate;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public record RuneTintSource(int defaultColor) implements TintSource {
    
    public static final MapCodec<RuneTintSource> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Codecs.RGB.fieldOf("default").forGetter(RuneTintSource::defaultColor)).apply(instance, RuneTintSource::new)
    );
    
    @Override
    public MapCodec<RuneTintSource> getCodec() {
        return CODEC;
    }

    @Override
    public int getTint(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user) {
        RuneComponent component = stack.get(ModComponents.RUNE_COMPONENT);
        if (component == null) return defaultColor;
        Formatting color;
        if (component.level() == 0) color = Formatting.RED;
        else {
            RuneTemplate template = RuneHelper.getRuneInfo(component.identifier());
            if (template == null) return defaultColor;
            int colorIndex = template.maxLevel() - component.level();
            color = new Formatting[] {
                    Formatting.LIGHT_PURPLE,
                    Formatting.AQUA,
                    Formatting.GREEN,
                    Formatting.YELLOW,
                    Formatting.WHITE
            }[colorIndex];
        }
        assert color.getColorValue() != null;
        return color.getColorValue() | 0xFF000000;
    }
}