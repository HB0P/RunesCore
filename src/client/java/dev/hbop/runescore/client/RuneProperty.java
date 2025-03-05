package dev.hbop.runescore.client;

import com.mojang.serialization.MapCodec;
import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record RuneProperty() implements SelectProperty<Identifier> {

    public static final SelectProperty.Type<RuneProperty, Identifier> TYPE = SelectProperty.Type.create(
            MapCodec.unit(new RuneProperty()), Identifier.CODEC
    );
    
    @Nullable
    @Override
    public Identifier getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ModelTransformationMode modelTransformationMode) {
        RuneComponent component = stack.get(ModComponents.RUNE_COMPONENT);
        if (component == null) return null;
        return component.identifier();
    }

    @Override
    public Type<? extends SelectProperty<Identifier>, Identifier> getType() {
        return TYPE;
    }
}