package dev.hbop.runescore.client.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class M_InGameHud {

    @Shadow protected abstract LivingEntity getRiddenEntity();
    @Shadow protected abstract int getHeartCount(@Nullable LivingEntity entity);

    /**
     * @author HB0P
     * @reason Remove experience bar
     */
    @Overwrite
    private void renderExperienceBar(DrawContext context, int x) {}
    
    // offset status bars
    @Redirect(
            method = "renderStatusBars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowHeight()I"
            )
    )
    private int drawGuiTexture(DrawContext context) {
        int offset = this.getHeartCount(this.getRiddenEntity()) == 0 ? 6 : 0;
        return context.getScaledWindowHeight() + offset;
    }
}
