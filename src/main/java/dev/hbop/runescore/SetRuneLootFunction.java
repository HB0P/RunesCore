package dev.hbop.runescore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.component.CorruptedRuneComponent;
import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Map;

public class SetRuneLootFunction extends ConditionalLootFunction {
    
    public static final MapCodec<SetRuneLootFunction> CODEC = RecordCodecBuilder.mapCodec(
            instance -> addConditionsField(instance).and(
                    instance.group(
                            Identifier.CODEC.fieldOf("id").forGetter(SetRuneLootFunction::getId),
                            LootNumberProviderTypes.CODEC.fieldOf("size").forGetter(SetRuneLootFunction::getSize),
                            Codecs.nonEmptyMap(Codec.unboundedMap(TagKey.codec(RegistryKeys.ITEM), Codecs.nonEmptyMap(Codec.unboundedMap(Enchantment.ENTRY_CODEC, Codec.intRange(1, 255))))).fieldOf("enchantments").forGetter(SetRuneLootFunction::getEnchantments),
                            Codecs.listOrSingle(Identifier.CODEC).optionalFieldOf("removes_runes", List.of()).forGetter(SetRuneLootFunction::getRemovesRunes),
                            Codecs.listOrSingle(Enchantment.ENTRY_CODEC).optionalFieldOf("removes_enchantments", List.of()).forGetter(SetRuneLootFunction::getRemovesEnchantments)
                    )
            ).apply(instance, SetRuneLootFunction::new)
    );

    public static final LootFunctionType<SetRuneLootFunction> TYPE = Registry.register(
            Registries.LOOT_FUNCTION_TYPE,
            RunesCore.identifier("set_rune"),
            new LootFunctionType<>(SetRuneLootFunction.CODEC)
    );

    private final Identifier id;
    private final LootNumberProvider size;
    private final Map<TagKey<Item>, Map<RegistryEntry<Enchantment>, Integer>> enchantments;
    private final List<Identifier> removesRunes;
    private final List<RegistryEntry<Enchantment>> removesEnchantments;
    
    protected SetRuneLootFunction(List<LootCondition> conditions, Identifier id, LootNumberProvider size, Map<TagKey<Item>, Map<RegistryEntry<Enchantment>, Integer>> enchantments, List<Identifier> removesRunes, List<RegistryEntry<Enchantment>> removesEnchantments) {
        super(conditions);
        this.id = id;
        this.size = size;
        this.enchantments = enchantments;
        this.removesRunes = removesRunes;
        this.removesEnchantments = removesEnchantments;
    }

    public Identifier getId() {
        return this.id;
    }
    
    public LootNumberProvider getSize() {
        return this.size;
    }

    public Map<TagKey<Item>, Map<RegistryEntry<Enchantment>, Integer>> getEnchantments() {
        return enchantments;
    }

    public List<Identifier> getRemovesRunes() {
        return removesRunes;
    }

    public List<RegistryEntry<Enchantment>> getRemovesEnchantments() {
        return removesEnchantments;
    }

    @Override
    public LootFunctionType<? extends ConditionalLootFunction> getType() {
        return TYPE;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        stack.set(ModComponents.RUNE_COMPONENT, new RuneComponent(id, size.nextInt(context), enchantments));
        if (!removesRunes.isEmpty() || !removesEnchantments.isEmpty()) {
            stack.set(ModComponents.CORRUPTED_RUNE_COMPONENT, new CorruptedRuneComponent(removesRunes, removesEnchantments));
        }
        stack.set(DataComponentTypes.ITEM_NAME, Text.translatable("item." + id.getNamespace() + ".rune." + id.getPath()));
        stack.set(DataComponentTypes.ITEM_MODEL, id.withPath("rune/" + id.getPath()));
        return stack;
    }
    
    public static void register() {}
}
