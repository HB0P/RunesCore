package dev.hbop.runescore.screen;

import dev.hbop.runescore.RunesCore;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreens {

    public static ScreenHandlerType<NewEnchantmentScreenHandler> NEW_ENCHANTMENT_SCREEN_HANDLER;

    public static void initialiseScreens() {
        NEW_ENCHANTMENT_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                RunesCore.identifier("enchantment"),
                new ScreenHandlerType<>(NewEnchantmentScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
    }
}