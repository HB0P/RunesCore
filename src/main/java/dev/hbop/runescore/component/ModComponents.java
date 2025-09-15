package dev.hbop.runescore.component;

import dev.hbop.runescore.RunesCore;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class ModComponents {

    private static final UnaryOperator<ComponentType.Builder<RuneComponent>> runeComponentOp = builder ->
            builder.codec(RuneComponent.CODEC);
    public static final ComponentType<RuneComponent> RUNE_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            RunesCore.identifier("rune"),
            runeComponentOp.apply(ComponentType.builder()).build()
    );

    private static final UnaryOperator<ComponentType.Builder<CorruptedRuneComponent>> corruptedRuneComponentOp = builder ->
            builder.codec(CorruptedRuneComponent.CODEC);
    public static final ComponentType<CorruptedRuneComponent> CORRUPTED_RUNE_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            RunesCore.identifier("corrupted_rune"),
            corruptedRuneComponentOp.apply(ComponentType.builder()).build()
    );

    private static final UnaryOperator<ComponentType.Builder<AppliedRunesComponent>> appliedRunesComponentOp = builder ->
            builder.codec(AppliedRunesComponent.CODEC);
    public static final ComponentType<AppliedRunesComponent> APPLIED_RUNES_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            RunesCore.identifier("applied_runes"),
            appliedRunesComponentOp.apply(ComponentType.builder()).build()
    );

    private static final UnaryOperator<ComponentType.Builder<Integer>> runeCapacityComponentOp = builder ->
            builder.codec(Codecs.POSITIVE_INT);
    public static final ComponentType<Integer> RUNE_CAPACITY_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            RunesCore.identifier("rune_capacity"),
            runeCapacityComponentOp.apply(ComponentType.builder()).build()
    );

    public static List<AbstractRuneComponent> getAbstractRuneComponents(ItemStack rune) {
        return new ArrayList<>(Stream.of(
                (AbstractRuneComponent) rune.get(ModComponents.RUNE_COMPONENT),
                rune.get(ModComponents.CORRUPTED_RUNE_COMPONENT)
        ).filter(Objects::nonNull).toList());
    }
    
    public static List<ComponentType<? extends AbstractRuneComponent>> getRuneComponentTypes() {
        return List.of(
                RUNE_COMPONENT,
                CORRUPTED_RUNE_COMPONENT
        );
    }
    
    public static void register() {}
}