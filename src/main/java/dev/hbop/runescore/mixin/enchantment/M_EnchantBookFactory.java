package dev.hbop.runescore.mixin.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TradeOffers.EnchantBookFactory.class)
public abstract class M_EnchantBookFactory {
    
    @Inject(
            method = "create",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injected(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir) {
        cir.setReturnValue(new TradeOffer(new TradedItem(Items.EMERALD, 3), new ItemStack(Items.BOOK), 12, 1, 0.05F));
    }
}