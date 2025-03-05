package dev.hbop.runescore.mixin.enchantment;

import com.llamalad7.mixinextras.sugar.Local;
import dev.hbop.runescore.screen.NewEnchantmentScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantingTableBlock.class)
public abstract class M_EnchantingTableBlock {

    // Make enchanting tables use new screen
    @Inject(
            method = "createScreenHandlerFactory",
            at = @At(value = "RETURN", ordinal = 0),
            cancellable = true
    )
    private void redirect(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<NamedScreenHandlerFactory> cir, @Local Text text) {
        cir.setReturnValue(new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, player) -> new NewEnchantmentScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), text
        ));
    }
}
