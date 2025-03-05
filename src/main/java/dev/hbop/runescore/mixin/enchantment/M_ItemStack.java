package dev.hbop.runescore.mixin.enchantment;

import dev.hbop.runescore.component.ModComponents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class M_ItemStack {

    @Shadow protected abstract <T extends TooltipAppender> void appendTooltip(ComponentType<T> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type);
    
    // add tooltip to runes and equipment with runes
    @Redirect(
            method = "getTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                    ordinal = 0
            )
    )
    private void redirect(ItemStack instance, ComponentType<ItemEnchantmentsComponent> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {
        this.appendTooltip(ModComponents.RUNE_COMPONENT, context, textConsumer, type);
        this.appendTooltip(ModComponents.RUNE_SLOTS_COMPONENT, context, textConsumer, type);
        this.appendTooltip(componentType, context, textConsumer, type);
    }
}
