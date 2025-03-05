package dev.hbop.runescore.loot;

import dev.hbop.runescore.RunesCore;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModLootFunctions {

    public static final LootFunctionType<SetRuneLootFunction> SET_RUNE_LOOT_FUNCTION = Registry.register(
            Registries.LOOT_FUNCTION_TYPE,
            RunesCore.identifier("set_rune"),
            new LootFunctionType<>(SetRuneLootFunction.CODEC)
    );

    public static void initialiseLootFunctions() {

    }
}