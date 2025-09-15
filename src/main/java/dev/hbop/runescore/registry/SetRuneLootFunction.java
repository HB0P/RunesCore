package dev.hbop.runescore.registry;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.RunesCore;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public class SetRuneLootFunction extends ConditionalLootFunction {
    
    public static final MapCodec<SetRuneLootFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> addConditionsField(instance).and(
                    RuneTemplate.ENTRY_CODEC.fieldOf("rune").forGetter(SetRuneLootFunction::getRune)
            ).apply(instance, SetRuneLootFunction::new)
    );

    public static final LootFunctionType<SetRuneLootFunction> TYPE = Registry.register(
            Registries.LOOT_FUNCTION_TYPE,
            RunesCore.identifier("set_rune"),
            new LootFunctionType<>(SetRuneLootFunction.CODEC)
    );

    private final RegistryEntry<RuneTemplate> rune;
    
    protected SetRuneLootFunction(List<LootCondition> conditions, RegistryEntry<RuneTemplate> rune) {
        super(conditions);
        this.rune = rune;
    }

    public RegistryEntry<RuneTemplate> getRune() {
        return this.rune;
    }

    @Override
    public LootFunctionType<? extends ConditionalLootFunction> getType() {
        return TYPE;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        stack.applyComponentsFrom(RuneTemplate.getComponents(this.rune));
        return stack;
    }
    
    public static void register() {}
}
