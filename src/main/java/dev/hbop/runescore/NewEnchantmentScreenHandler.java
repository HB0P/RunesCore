package dev.hbop.runescore;

import dev.hbop.runescore.component.AbstractRuneComponent;
import dev.hbop.runescore.component.AppliedRunesComponent;
import dev.hbop.runescore.component.ModComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class NewEnchantmentScreenHandler extends ForgingScreenHandler {

    public static ScreenHandlerType<NewEnchantmentScreenHandler> TYPE = Registry.register(
            Registries.SCREEN_HANDLER,
            RunesCore.identifier("enchantment"),
            new ScreenHandlerType<>(NewEnchantmentScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
    );
    
    public NewEnchantmentScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }
    
    public NewEnchantmentScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(TYPE, syncId, playerInventory, context, getForgingSlotsManager());
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {
        // count total size of input runes
        int size = 0;
        for (AbstractRuneComponent runeComponent : ModComponents.getAbstractRuneComponents(this.input.getStack(1))) {
            size += runeComponent.size();
        }
        
        // update outputs
        this.input.setStack(0, ItemStack.EMPTY);
        this.input.setStack(1, ItemStack.EMPTY);
        this.input.getStack(2).decrement(size);
        
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

        // get rune components
        List<AbstractRuneComponent> runeComponents = ModComponents.getAbstractRuneComponents(rune);
        if (runeComponents.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }

        // count total size of input runes
        int size = 0;
        for (AbstractRuneComponent runeComponent : runeComponents) {
            size += runeComponent.size();
        }
        
        // ensure available lapis
        if (lapis.getCount() < size) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }

        // ensure available rune slots
        AppliedRunesComponent appliedRunesComponent = item.getOrDefault(ModComponents.APPLIED_RUNES_COMPONENT, new AppliedRunesComponent(Map.of()));
        Integer runeCapacity = item.get(ModComponents.RUNE_CAPACITY_COMPONENT);
        if (runeCapacity != null && appliedRunesComponent.getTotalSize() + size > runeCapacity) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }
        
        // create applied runes component
        runeComponents.sort(Comparator.comparingInt(AbstractRuneComponent::applicationPriority));
        boolean changed = false;
        for (AbstractRuneComponent runeComponent : runeComponents) {
            AppliedRunesComponent result = runeComponent.apply(appliedRunesComponent, item::isIn);
            if (result != null) {
                changed = true;
                appliedRunesComponent = result;
            }
        }
        if (!changed) {
            this.output.setStack(0, ItemStack.EMPTY);
            return;
        }
        
        // create enchantments component
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        for (AppliedRunesComponent.AppliedRune appliedRune : appliedRunesComponent.runes().values()) {
            for (Map.Entry<RegistryEntry<Enchantment>, Integer> enchantment : appliedRune.enchantments().entrySet()) {
                if (enchantment.getValue() > builder.getLevel(enchantment.getKey())) {
                    builder.set(enchantment.getKey(), enchantment.getValue());
                }
            }
        }
        ItemEnchantmentsComponent enchantmentsComponent = builder.build();

        // apply components
        ItemStack output = item.copy();
        output.set(ModComponents.APPLIED_RUNES_COMPONENT, appliedRunesComponent.runes().isEmpty() ? null : appliedRunesComponent);
        output.set(DataComponentTypes.ENCHANTMENTS, enchantmentsComponent);
        TooltipDisplayComponent tooltipDisplayComponent = output.getOrDefault(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT);
        output.set(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplayComponent.with(DataComponentTypes.ENCHANTMENTS, !enchantmentsComponent.isEmpty()));
        
        this.output.setStack(0, output);
    }
    
    private static ForgingSlotsManager getForgingSlotsManager() {
        return ForgingSlotsManager.builder()
                .input(0, 26, 47, stack -> true)
                .input(1, 67, 47, stack -> ModComponents.getRuneComponentTypes().stream().anyMatch(stack::contains))
                .input(2, 85, 47, stack -> stack.isOf(Items.LAPIS_LAZULI))
                .output(3, 134, 47)
                .build();
    }
    
    public static void register() {}
}
