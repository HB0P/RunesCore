package dev.hbop.runescore.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(AnvilScreenHandler.class)
public abstract class M_AnvilScreenHandler {
    
    // enchantments cannot be combined at anvils
    @Redirect(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/component/type/ItemEnchantmentsComponent;getEnchantmentEntries()Ljava/util/Set;"
            )
    )
    private Set<Object2IntMap.Entry<RegistryEntry<Enchantment>>> getEnchantmentEntries(ItemEnchantmentsComponent instance) {
        return Set.of();
    }
}
