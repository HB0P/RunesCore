package dev.hbop.runescore.registry;

import com.mojang.serialization.Codec;
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
import net.minecraft.util.dynamic.Codecs;

import java.util.List;

public class SetRuneLootFunction extends ConditionalLootFunction {
    
    public static final MapCodec<SetRuneLootFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> addConditionsField(instance).and(
                    instance.group(
                            Codecs.listOrSingle(RuneTemplate.ENTRY_CODEC).fieldOf("rune").forGetter(SetRuneLootFunction::getRune),
                            Codec.BOOL.optionalFieldOf("set_name", true).forGetter(SetRuneLootFunction::getSetName),
                            Codec.BOOL.optionalFieldOf("set_model", true).forGetter(SetRuneLootFunction::getSetModel)
                    )
            ).apply(instance, SetRuneLootFunction::new)
    );

    public static final LootFunctionType<SetRuneLootFunction> TYPE = Registry.register(
            Registries.LOOT_FUNCTION_TYPE,
            RunesCore.identifier("set_rune"),
            new LootFunctionType<>(SetRuneLootFunction.CODEC)
    );

    private final List<RegistryEntry<RuneTemplate>> rune;
    private final boolean setName;
    private final boolean setModel;
    
    protected SetRuneLootFunction(List<LootCondition> conditions, List<RegistryEntry<RuneTemplate>> rune, boolean setName, boolean setModel) {
        super(conditions);
        this.rune = rune;
        this.setName = setName;
        this.setModel = setModel;
    }

    public List<RegistryEntry<RuneTemplate>> getRune() {
        return this.rune;
    }
    
    public boolean getSetName() {
        return this.setName;
    }
    
    public boolean getSetModel() {
        return this.setModel;
    }

    @Override
    public LootFunctionType<? extends ConditionalLootFunction> getType() {
        return TYPE;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        RegistryEntry<RuneTemplate> chosenRune;
        if (rune.size() == 1) chosenRune = rune.getFirst();
        else chosenRune = rune.get(context.getRandom().nextInt(rune.size()));

        stack.applyComponentsFrom(RuneTemplate.getComponents(chosenRune, setName, setModel));
        return stack;
    }
    
    public static void register() {}
}
