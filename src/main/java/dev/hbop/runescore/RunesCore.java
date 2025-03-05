package dev.hbop.runescore;

import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.helper.RuneHelper;
import dev.hbop.runescore.item.ModItems;
import dev.hbop.runescore.loot.ModLootFunctions;
import dev.hbop.runescore.recipe.ModRecipes;
import dev.hbop.runescore.screen.ModScreens;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunesCore implements ModInitializer {

    public static final String MOD_ID = "runescore";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        RuneHelper.registerReloadListener();
        ModComponents.initialiseComponents();
        ModItems.initialiseItems();
        ModLootFunctions.initialiseLootFunctions();
        ModScreens.initialiseScreens();
        ModRecipes.registerRecipes();
    }

    public static Identifier identifier(String id) {
        return Identifier.of(MOD_ID, id);
    }

    @SuppressWarnings("unused")
    public static void log(Object object) {
        if (object == null) LOGGER.info(null);
        else LOGGER.info(object.toString());
    }
}
