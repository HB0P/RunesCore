package dev.hbop.runescore.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.helper.RuneHelper;
import dev.hbop.runescore.helper.RuneTemplate;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.List;

public class SetRuneLootFunction extends ConditionalLootFunction {
    
    public static final MapCodec<SetRuneLootFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> addConditionsField(instance).and(
                            instance.group(
                                    LootNumberProviderTypes.CODEC.fieldOf("level").forGetter(SetRuneLootFunction::getLevel),
                                    Identifier.CODEC.fieldOf("identifier").forGetter(SetRuneLootFunction::getID)
                            )
                    )
                    .apply(instance, SetRuneLootFunction::new)
    );
    
    private final LootNumberProvider level;
    private final Identifier id;
    
    protected SetRuneLootFunction(List<LootCondition> conditions, LootNumberProvider level, Identifier id) {
        super(conditions);
        this.level = level;
        this.id = id;
    }
    
    public LootNumberProvider getLevel() {
        return level;
    }
    
    public Identifier getID() {
        return id;
    }

    @Override
    public LootFunctionType<? extends ConditionalLootFunction> getType() {
        return ModLootFunctions.SET_RUNE_LOOT_FUNCTION;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        RuneTemplate runeTemplate = RuneHelper.getRuneInfo(id);
        if (runeTemplate == null) return stack;
        
        stack.applyComponentsFrom(
                runeTemplate.getComponents(
                        level.nextInt(context), 
                        context.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT)
                )
        );
        return stack;
    }

    public static Builder builder(LootNumberProvider level, Identifier id) {
        return new Builder(level, id);
    }

    public static class Builder extends ConditionalLootFunction.Builder<Builder> {
        private final LootNumberProvider level;
        private final Identifier id;

        Builder(LootNumberProvider level, Identifier id) {
            this.level = level;
            this.id = id;
        }

        @Override
        public LootFunction build() {
            return new SetRuneLootFunction(this.getConditions(), level, id);
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }
    }
}
