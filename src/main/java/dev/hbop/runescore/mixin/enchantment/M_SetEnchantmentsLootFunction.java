package dev.hbop.runescore.mixin.enchantment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SetEnchantmentsLootFunction.class)
public abstract class M_SetEnchantmentsLootFunction {

    // remove enchanted books from loot
    @Inject(
            method = "process",
            at = @At("HEAD"),
            cancellable = true
    )
    private void process(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        if (stack.isOf(Items.BOOK)) cir.setReturnValue(stack);
    }
}
