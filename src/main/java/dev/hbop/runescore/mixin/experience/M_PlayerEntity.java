package dev.hbop.runescore.mixin.experience;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PlayerEntity.class)
public abstract class M_PlayerEntity {
    
    /**
     * @author HB0P
     * @reason Remove experience
     */
    @Overwrite
    public void addExperience(int experience) {}
    
    /**
     * @author HB0P
     * @reason Remove experience
     */
    @Overwrite
    public void addExperienceLevels(int levels) {}
}
