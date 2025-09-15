package dev.hbop.runescore;

import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.registry.RuneTemplate;
import dev.hbop.runescore.registry.SetRuneLootFunction;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunesCore implements ModInitializer {

    public static final String MOD_ID = "runescore";
    @SuppressWarnings("unused")
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    @Override
    public void onInitialize() {
        ModComponents.register();
        SetRuneLootFunction.register();
        NewEnchantmentScreenHandler.register();
        RuneCorruptingRecipe.register();
        RuneTemplate.register();

        Items.register(
                RegistryKey.of(RegistryKeys.ITEM, RunesCore.identifier("rune")),
                Item::new,
                new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)
        );
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
