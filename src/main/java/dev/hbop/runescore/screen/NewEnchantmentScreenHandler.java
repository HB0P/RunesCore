package dev.hbop.runescore.screen;

import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import dev.hbop.runescore.component.RuneSlotsComponent;
import dev.hbop.runescore.helper.RuneHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewEnchantmentScreenHandler extends ForgingScreenHandler {
    
    public NewEnchantmentScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }
    
    public NewEnchantmentScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreens.NEW_ENCHANTMENT_SCREEN_HANDLER, syncId, playerInventory, context, getForgingSlotsManager());
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        RuneComponent runeComponent = this.input.getStack(1).get(ModComponents.RUNE_COMPONENT);
        assert runeComponent != null;
        this.input.setStack(0, ItemStack.EMPTY);
        this.input.setStack(1, ItemStack.EMPTY);
        this.input.getStack(2).decrement(runeComponent.size());
        
        // sound
        this.context.run((world, pos) -> world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F));
    }

    @Override
    protected boolean canUse(BlockState state) {
        return state.isOf(Blocks.ENCHANTING_TABLE);
    }

    @Override
    public void updateResult() {
        ItemStack item = this.input.getStack(0);
        ItemStack rune = this.input.getStack(1);
        ItemStack lapis = this.input.getStack(2);
        if (item.isEmpty() || rune.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }

        // get rune component
        RuneComponent runeComponent = rune.get(ModComponents.RUNE_COMPONENT);
        if (runeComponent == null) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }

        // ensure available lapis
        if (lapis.getCount() < runeComponent.size()) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }

        // ensure available rune slots
        RuneSlotsComponent inputRuneSlotsComponent = item.get(ModComponents.RUNE_SLOTS_COMPONENT);
        Map<Identifier, Integer> usedSlots;
        int maxSlots;
        int totalSlots = 0;
        if (inputRuneSlotsComponent == null) {
            usedSlots = Map.of(runeComponent.identifier(), runeComponent.size());
            maxSlots = RuneHelper.getMaxRuneSlots(item);
            totalSlots = runeComponent.size();
        } else {
            usedSlots = new HashMap<>(inputRuneSlotsComponent.usedSlots());
            maxSlots = inputRuneSlotsComponent.maxSlots();
            usedSlots.put(runeComponent.identifier(), runeComponent.size());
            for (int c : usedSlots.values()) {
                totalSlots += c;
                if (totalSlots > maxSlots) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    return;
                }
            }
        }
        
        // get enchantments to apply
        List<RegistryEntry<Enchantment>> enchantments = RuneHelper.getEnchantmentsFor(item, runeComponent);
        if (enchantments == null) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }
        
        // apply enchantments
        boolean modified = false;
        ItemStack output = item.copy();
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(output));
        for (RegistryEntry<Enchantment> enchantment : enchantments) {
            // ensure item does not already have that enchantment at the same level
            if (EnchantmentHelper.getLevel(enchantment, item) != runeComponent.level()) {
                modified = true;
                builder.set(enchantment, runeComponent.level());
            }
        }
        if (modified) {
            EnchantmentHelper.set(output, builder.build());
            output.set(ModComponents.RUNE_SLOTS_COMPONENT, totalSlots == 0 ? null : new RuneSlotsComponent(usedSlots, maxSlots));
            this.output.setStack(0, output);
        }
        else {
            this.output.setStack(0, ItemStack.EMPTY);
        }
    }
    
    private static ForgingSlotsManager getForgingSlotsManager() {
        return ForgingSlotsManager.builder()
                .input(0, 26, 47, stack -> true)
                .input(1, 67, 47, stack -> stack.getComponents().contains(ModComponents.RUNE_COMPONENT))
                .input(2, 85, 47, stack -> stack.isOf(Items.LAPIS_LAZULI))
                .output(3, 134, 47)
                .build();
    }
}
