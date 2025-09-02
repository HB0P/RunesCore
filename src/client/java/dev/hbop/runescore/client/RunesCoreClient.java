package dev.hbop.runescore.client;

import dev.hbop.runescore.NewEnchantmentScreenHandler;
import dev.hbop.runescore.client.screen.NewEnchantmentScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class RunesCoreClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(NewEnchantmentScreenHandler.TYPE, NewEnchantmentScreen::new);
    }
}
