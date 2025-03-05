package dev.hbop.runescore.item;

import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class RuneItem extends Item {
    public RuneItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public Text getName(ItemStack stack) {
        RuneComponent component = stack.get(ModComponents.RUNE_COMPONENT);
        if (component == null) return super.getName(stack);
        return Text.translatable(
                "item.runescore." + (component.level() == 0 ? "corrupted_rune_prefix" : "rune_prefix"),
                Text.translatable("item.runescore.rune." + component.identifier().toTranslationKey())
        );
    }
}