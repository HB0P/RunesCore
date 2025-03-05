package dev.hbop.runescore.mixin.experience;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class M_ServerWorld {

    @Inject(
            method = "spawnEntity",
            at = @At("HEAD"),
            cancellable = true
    )
    private void spawnEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ExperienceOrbEntity) {
            cir.setReturnValue(false);
        }
    }
}
