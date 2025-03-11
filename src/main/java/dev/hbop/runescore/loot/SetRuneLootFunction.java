package dev.hbop.runescore.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.helper.RuneTemplate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public class SetRuneLootFunction extends ConditionalLootFunction {
    
    public static final MapCodec<SetRuneLootFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> addConditionsField(instance).and(
                            instance.group(
                                    RuneTemplate.ENTRY_CODEC.fieldOf("template").forGetter(SetRuneLootFunction::getTemplate),
                                    LootNumberProviderTypes.CODEC.fieldOf("level").forGetter(SetRuneLootFunction::getLevel)
                            )
                    )
                    .apply(instance, SetRuneLootFunction::new)
    );

    private final RegistryEntry<RuneTemplate> template;
    private final LootNumberProvider level;
    
    protected SetRuneLootFunction(List<LootCondition> conditions, RegistryEntry<RuneTemplate> template, LootNumberProvider level) {
        super(conditions);
        this.level = level;
        this.template = template;
    }

    public RegistryEntry<RuneTemplate> getTemplate() {
        return template;
    }
    
    public LootNumberProvider getLevel() {
        return level;
    }

    @Override
    public LootFunctionType<? extends ConditionalLootFunction> getType() {
        return ModLootFunctions.SET_RUNE_LOOT_FUNCTION;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        stack.applyComponentsFrom(template.value().getComponents(template.getKey().orElseThrow().getValue(), level.nextInt(context)));
        return stack;
    }
}
