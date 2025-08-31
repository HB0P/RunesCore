package dev.hbop.runescore.client.screen;

import dev.hbop.runescore.RunesCore;
import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import dev.hbop.runescore.helper.RuneHelper;
import dev.hbop.runescore.screen.NewEnchantmentScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
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
        this.drawBook(context, this.width / 2f, (this.height - this.backgroundHeight) / 2f + 26, delta);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        ItemStack item = handler.getSlot(0).getStack();
        ItemStack rune = handler.getSlot(1).getStack();
        ItemStack lapis = handler.getSlot(2).getStack();
        ItemStack output = handler.getSlot(handler.getResultSlotIndex()).getStack();
        if (item.isEmpty() || rune.isEmpty() || lapis.isEmpty()) return;
        
        Text text = null;
        int color = 16736352;
        RuneComponent runeComponent = rune.get(ModComponents.RUNE_COMPONENT);
        if (runeComponent == null) {
            text = Text.translatable("container.enchantment.incompatible");
        }
        else {
            List<RegistryEntry<Enchantment>> enchantments = RuneHelper.getEnchantmentsFor(item, runeComponent);
            if (enchantments == null) {
                text = Text.translatable("container.enchantment.incompatible");
            }
            else if (EnchantmentHelper.getLevel(enchantment, item) == runeComponent.level()) {
                text = Text.translatable("container.enchantment.equal");
            }
            else if (lapis.getCount() < runeComponent.size()) {
                text = Text.translatable("container.enchantment.inadequate_lapis");
            }
            else if (output.isEmpty()) {
                text = Text.translatable("container.enchantment.full");
            }
        }
        if (text != null) {
            int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
            context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
            context.drawTextWithShadow(this.textRenderer, text, k, 69, color);
        }
    }
    
    @Override
    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if ((this.handler.getSlot(0).hasStack() && this.handler.getSlot(1).hasStack()) && this.handler.getSlot(2).hasStack() && !this.handler.getSlot(this.handler.getResultSlotIndex()).hasStack()) {
            context.drawGuiTexture(RenderLayer::getGuiTextured, ERROR_TEXTURE, x + 104, y + 45, 28, 21);
        }
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        doTick();
    }

    private void drawBook(DrawContext context, float x, float y, float delta) {
        float f = MathHelper.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
        float g = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle);
        DiffuseLighting.enableGuiShaderLighting();
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 100.0F);
        context.getMatrices().scale(-40.0F, 40.0F, 40.0F);
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(25.0F));
        context.getMatrices().translate((1.0F - f) * 0.2F, (1.0F - f) * 0.1F, (1.0F - f) * 0.25F);
        float i = -(1.0F - f) * 90.0F - 90.0F;
        context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i));
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        float j = MathHelper.clamp(MathHelper.fractionalPart(g + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
        float k = MathHelper.clamp(MathHelper.fractionalPart(g + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
        this.BOOK_MODEL.setPageAngles(0.0F, j, k, f);
        context.draw((vertexConsumers) -> {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.BOOK_MODEL.getLayer(BOOK_TEXTURE));
            this.BOOK_MODEL.render(context.getMatrices(), vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV);
        });
        context.draw();
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
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
