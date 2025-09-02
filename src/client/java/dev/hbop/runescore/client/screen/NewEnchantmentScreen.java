package dev.hbop.runescore.client.screen;

import dev.hbop.runescore.NewEnchantmentScreenHandler;
import dev.hbop.runescore.RunesCore;
import dev.hbop.runescore.component.AbstractRuneComponent;
import dev.hbop.runescore.component.AppliedRunesComponent;
import dev.hbop.runescore.component.ModComponents;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class NewEnchantmentScreen extends ForgingScreen<NewEnchantmentScreenHandler> {

    private static final Identifier TEXTURE = RunesCore.identifier("textures/gui/container/enchanting_table.png");
    private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/anvil/error");
    private final Random random = Random.create();
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack oldItemStack = ItemStack.EMPTY;
    private ItemStack oldRuneStack = ItemStack.EMPTY;
    private int oldLapisCount = 0;
    private static final Identifier BOOK_TEXTURE = Identifier.ofVanilla("textures/entity/enchanting_table_book.png");
    private BookModel BOOK_MODEL;

    public NewEnchantmentScreen(NewEnchantmentScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title, TEXTURE);
    }

    @Override
    protected void init() {
        super.init();
        assert this.client != null;
        this.BOOK_MODEL = new BookModel(this.client.getLoadedEntityModels().getModelPart(EntityModelLayers.BOOK));
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        super.drawBackground(context, delta, mouseX, mouseY);
        
        // draw book
        assert this.client != null;
        float f = this.client.getRenderTickCounter().getTickProgress(false);
        float g = MathHelper.lerp(f, this.pageTurningSpeed, this.nextPageTurningSpeed);
        float h = MathHelper.lerp(f, this.pageAngle, this.nextPageAngle);
        int i = this.x + this.backgroundWidth / 2 - 19;
        int j = this.y + 10;
        int k = i + 38;
        int l = j + 31;
        context.addBookModel(this.BOOK_MODEL, BOOK_TEXTURE, 40.0F, g, h, i, j, k, l);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        ItemStack item = handler.getSlot(0).getStack();
        ItemStack rune = handler.getSlot(1).getStack();
        ItemStack lapis = handler.getSlot(2).getStack();
        ItemStack output = handler.getSlot(handler.getResultSlotIndex()).getStack();
        if (item.isEmpty() || rune.isEmpty() || !output.isEmpty()) return;
        
        Text text;
        
        // get rune components
        List<AbstractRuneComponent> runeComponents = ModComponents.getAbstractRuneComponents(rune);
        if (runeComponents.isEmpty()) {
            text = Text.translatable("container.enchantment.incompatible");
        }
        else {
            // count total size of input runes
            int size = 0;
            for (AbstractRuneComponent runeComponent : runeComponents) {
                size += runeComponent.size();
            }

            // ensure available lapis
            if (lapis.getCount() < size) {
                text = Text.translatable("container.enchantment.inadequate_lapis");
            }
            else {
                // ensure available rune slots
                AppliedRunesComponent appliedRunesComponent = item.get(ModComponents.APPLIED_RUNES_COMPONENT);
                Integer runeCapacity = item.get(ModComponents.RUNE_CAPACITY_COMPONENT);
                if (appliedRunesComponent != null && runeCapacity != null && appliedRunesComponent.getTotalSize() + size > runeCapacity) {
                    text = Text.translatable("container.enchantment.full");
                }
                else {
                    text = Text.translatable("container.enchantment.incompatible");
                }
            }
        }

        int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
        context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
        context.drawTextWithShadow(this.textRenderer, text, k, 69, -40864);
    }
    
    @Override
    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if ((this.handler.getSlot(0).hasStack() && this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, x + 104, y + 45, 28, 21);
        }
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        doTick();
    }

    private void doTick() {
        // flip book
        ItemStack itemStack = this.handler.getSlot(0).getStack();
        ItemStack runeStack = this.handler.getSlot(1).getStack();
        int lapisCount = this.handler.getSlot(2).getStack().getCount();
        if (!ItemStack.areEqual(itemStack, oldItemStack) || !ItemStack.areEqual(runeStack,oldRuneStack) || lapisCount != oldLapisCount) {
            this.oldItemStack = itemStack;
            this.oldRuneStack = runeStack;
            this.oldLapisCount = lapisCount;
            do {
                this.approximatePageAngle = this.approximatePageAngle + (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.nextPageAngle <= this.approximatePageAngle + 1.0F && this.nextPageAngle >= this.approximatePageAngle - 1.0F);
        }
        
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        
        // open book
        if (this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack()) {
            this.nextPageTurningSpeed += 0.2F;
        } else {
            this.nextPageTurningSpeed -= 0.2F;
        }

        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4F;
        f = MathHelper.clamp(f, -0.2F, 0.2F);
        this.pageRotationSpeed = this.pageRotationSpeed + (f - this.pageRotationSpeed) * 0.9F;
        this.nextPageAngle = this.nextPageAngle + this.pageRotationSpeed;
    }
}
