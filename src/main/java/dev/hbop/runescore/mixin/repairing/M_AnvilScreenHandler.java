package dev.hbop.runescore.mixin.repairing;

import dev.hbop.runescore.item.ModItemTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.ForgingSlotsManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(AnvilScreenHandler.class)
public abstract class M_AnvilScreenHandler {
    
    @Unique
    private static final Map<Item, Item> REPAIR_ITEMS = Map.of(
            Items.TRIDENT, Items.PRISMARINE_SHARD,
            Items.SHEARS, Items.IRON_INGOT,
            Items.BOW, Items.STRING,
            Items.CROSSBOW, Items.IRON_INGOT,
            Items.FISHING_ROD, Items.STRING,
            Items.CARROT_ON_A_STICK, Items.CARROT,
            Items.WARPED_FUNGUS_ON_A_STICK, Items.WARPED_FUNGUS,
            Items.FLINT_AND_STEEL, Items.FLINT,
            Items.SHIELD, Items.IRON_INGOT,
            Items.BRUSH, Items.FEATHER
    );
    @Unique
    private static final Set<Item> NETHERITE_EQUIPMENT = Set.of(
            Items.NETHERITE_SWORD,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_AXE,
            Items.NETHERITE_SHOVEL,
            Items.NETHERITE_HOE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS
    );
    
    /**
     * @author HB0P
     * @reason No experience cost on anvils
     */
    @Overwrite
    public boolean canTakeOutput(PlayerEntity player, boolean present) {
        return true;
    }
    
    // no 40 level cap
    @Redirect(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/Property;get()I"
            )
    )
    private int updateResult(Property instance) {
        return 0;
    }
    
    // only allow repair materials in the second slot
    @Redirect(
            method = "getForgingSlotsManager",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;",
                    ordinal = 1
            )
    )
    private static ForgingSlotsManager.Builder getForgingSlotsManager(ForgingSlotsManager.Builder instance, int slotId, int x, int y, Predicate<ItemStack> mayPlace) {
        return instance.input(slotId, x, y, stack -> stack.isIn(ModItemTags.ANVIL_REPAIR_MATERIALS));
    }
    
    // more repair materials
    @Redirect(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;canRepairWith(Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private boolean canRepairWith(ItemStack stack, ItemStack ingredient) {
        for (Item item : REPAIR_ITEMS.keySet()) {
            if (stack.isOf(item) && ingredient.isOf(REPAIR_ITEMS.get(item))) {
                return true;
            }
        }
        for (Item item : NETHERITE_EQUIPMENT) {
            if (stack.isOf(item)) {
                return ingredient.isOf(Items.DIAMOND);
            }
        }
        return stack.canRepairWith(ingredient);
    }
    
    // remove vanilla system where each item repairs 1/4 durability
    @ModifyConstant(
            method = "updateResult",
            constant = @Constant(intValue = 4)
    )
    private int modify4(int constant) {
        return 1;
    }
    
    // each item repairs a custom amount of durability
    @Redirect(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I"
            ),
            slice = @Slice(
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I",
                            ordinal = 1
                    )
            )
    )
    private int getMaxDamage(ItemStack stack) {
        int cost;
        if (stack.isIn(ItemTags.SHOVELS) || stack.isIn(ItemTags.HOES) || stack.isOf(Items.SHEARS) || stack.isOf(Items.CARROT_ON_A_STICK) || stack.isOf(Items.WARPED_FUNGUS_ON_A_STICK) || stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.SHIELD) || stack.isOf(Items.BRUSH)) cost = 1;
        else if (stack.isIn(ItemTags.SWORDS) || stack.isIn(ItemTags.AXES) || stack.isIn(ItemTags.PICKAXES) || stack.isOf(Items.FISHING_ROD) || stack.isOf(Items.BOW) || stack.isOf(Items.CROSSBOW)) cost = 2;
        else cost = 4;
        return stack.getMaxDamage() / cost;
    }
}
