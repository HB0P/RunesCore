package dev.hbop.runescore.client;

import dev.hbop.runescore.RunesCore;
import dev.hbop.runescore.client.screen.NewEnchantmentScreen;
import dev.hbop.runescore.screen.ModScreens;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.item.tint.TintSourceTypes;

public class RunesCoreClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreens.NEW_ENCHANTMENT_SCREEN_HANDLER, NewEnchantmentScreen::new);
        TintSourceTypes.ID_MAPPER.put(RunesCore.identifier("rune"), RuneTintSource.CODEC);
    }
}
