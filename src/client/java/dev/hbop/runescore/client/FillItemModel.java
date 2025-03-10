package dev.hbop.runescore.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.hbop.runescore.RunesCore;
import dev.hbop.runescore.component.ModComponents;
import dev.hbop.runescore.component.RuneComponent;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FillItemModel implements ItemModel {
    
    private final IdentifierTemplate model;
    private final BakeContext context;
    private final List<TintSource> tints;
    private final BakedModel fallback;

    FillItemModel(IdentifierTemplate model, BakeContext context, List<TintSource> tints, BakedModel fallback) {
        this.model = model;
        this.context = context;
        this.tints = tints;
        this.fallback = fallback;
        RunesCore.log("Created fill item model");
    }

    @Override
    public void update(
            ItemRenderState state,
            ItemStack stack,
            ItemModelManager resolver,
            ModelTransformationMode transformationMode,
            @Nullable ClientWorld world,
            @Nullable LivingEntity user,
            int seed
    ) {
        ItemRenderState.LayerRenderState layerRenderState = state.newLayer();
        if (stack.hasGlint()) {
            layerRenderState.setGlint(shouldUseSpecialGlint(stack) ? ItemRenderState.Glint.SPECIAL : ItemRenderState.Glint.STANDARD);
        }

        int i = this.tints.size();
        int[] is = layerRenderState.initTints(i);

        for (int j = 0; j < i; j++) {
            is[j] = this.tints.get(j).getTint(stack, world, user);
        }

        RenderLayer renderLayer = RenderLayers.getItemLayer(stack);
        Identifier fill = getItemFill(stack);
        BakedModel bakedModel;
        if (fill == null) {
            bakedModel = fallback;
        }
        else {
            bakedModel = this.context.bake(model.parse(fill));
        }
        layerRenderState.setModel(bakedModel, renderLayer);
    }

    private static boolean shouldUseSpecialGlint(ItemStack stack) {
        return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
    }
    
    private Identifier getItemFill(ItemStack stack) {
        RuneComponent component = stack.get(ModComponents.RUNE_COMPONENT);
        if (component == null) return null;
        return component.identifier();
    }
    
    public record Unbaked(IdentifierTemplate model, List<TintSource> tints, Identifier fallback) implements ItemModel.Unbaked {
        public static final MapCodec<FillItemModel.Unbaked> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        IdentifierTemplate.CODEC.fieldOf("model").forGetter(FillItemModel.Unbaked::model),
                        TintSourceTypes.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(FillItemModel.Unbaked::tints),
                        Identifier.CODEC.fieldOf("fallback").forGetter(FillItemModel.Unbaked::fallback)
                ).apply(instance, FillItemModel.Unbaked::new)
        );
        
        @Override
        public MapCodec<? extends ItemModel.Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public ItemModel bake(BakeContext context) {
            return new FillItemModel(this.model, context, this.tints, context.bake(this.fallback));
        }

        @Override
        public void resolve(Resolver resolver) {
            resolver.resolve(this.fallback);
        }
    }
    
    public static class IdentifierTemplate {
        public static final Codec<IdentifierTemplate> CODEC = Codec.STRING.comapFlatMap(
                IdentifierTemplate::validate, 
                IdentifierTemplate::toString
        ).stable();
        
        private final String namespaceTemplate;
        private final String pathTemplate;
        
        public IdentifierTemplate(String namespaceTemplate, String pathTemplate) {
            this.namespaceTemplate = namespaceTemplate;
            this.pathTemplate = pathTemplate;
        }
        
        public Identifier parse(Identifier values) {
            return Identifier.of(
                    namespaceTemplate.replace("%n", values.getNamespace()).replace("%p", values.getPath()),
                    pathTemplate.replace("%n", values.getNamespace()).replace("%p", values.getPath())
            );
        }
        
        @Override
        public String toString() {
            return namespaceTemplate + ":" + pathTemplate;
        }
        
        public static DataResult<IdentifierTemplate> validate(String template) {
            int i = template.indexOf(':');
            if (i > 0 && i < template.length() - 1) {
                String namespace = template.substring(0, i);
                String path = template.substring(i + 1);
                return DataResult.success(new IdentifierTemplate(namespace, path));
            } else {
                return DataResult.error(() -> "Identifier template must be seperated by '':'");
            }
        }
    }
}