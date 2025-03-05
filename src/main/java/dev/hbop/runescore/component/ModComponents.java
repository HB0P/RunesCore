package dev.hbop.runescore.component;

import dev.hbop.runescore.RunesCore;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.UnaryOperator;

public class ModComponents {

    private static final UnaryOperator<ComponentType.Builder<RuneComponent>> runeComponentOp = builder ->
            builder.codec(RuneComponent.CODEC);
    public static final ComponentType<RuneComponent> RUNE_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            RunesCore.identifier("rune"),
            runeComponentOp.apply(ComponentType.builder()).build()
    );
    private static final UnaryOperator<ComponentType.Builder<RuneSlotsComponent>> runeSlotsComponentOp = builder ->
            builder.codec(RuneSlotsComponent.CODEC);
    public static final ComponentType<RuneSlotsComponent> RUNE_SLOTS_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            RunesCore.identifier("rune_slots"),
            runeSlotsComponentOp.apply(ComponentType.builder()).build()
    );

    public static void initialiseComponents() {

    }
}