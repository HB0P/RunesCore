package dev.hbop.runescore.component;

import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface AbstractRuneComponent extends TooltipAppender {

    @Nullable AppliedRunesComponent apply(AppliedRunesComponent appliedRunesComponent, Predicate<TagKey<Item>> predicate);
    
    CorruptedRuneComponent corrupt();
    
    int size();
    
    int applicationPriority();
    
    int corruptingPriority();
}