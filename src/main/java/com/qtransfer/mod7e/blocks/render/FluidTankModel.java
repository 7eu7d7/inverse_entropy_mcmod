package com.qtransfer.mod7e.blocks.render;

import com.google.common.collect.ImmutableList;
import com.qtransfer.mod7e.Constant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.function.Function;

public class FluidTankModel implements IModel{
    public static final ResourceLocation TEXTURE_TANK = new ResourceLocation(Constant.item("blocks/fluid_tank"));

    public static final ModelResourceLocation MODEL_TANK = new ModelResourceLocation(Constant.item("fluid_tank_core"));

    // return all the textures used by this model (not strictly needed for this example because we load all the subcomponent
    //   models during the bake anyway)
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.copyOf(new ResourceLocation[]{MODEL_TANK});
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableList.copyOf(new ResourceLocation[]{TEXTURE_TANK});
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

        try {
            IModel subComponent = ModelLoaderRegistry.getModel(MODEL_TANK);
            return new FluidTankBakedModel(ModelLoaderRegistry.getMissingModel().bake(state,format,bakedTextureGetter),
                    subComponent.bake(state,format,bakedTextureGetter),
                    Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(TEXTURE_TANK.toString()));
            //ModelLoaderRegistry.getModel(new ModelResourceLocation(Constant.item("models/block/air_ionizer"))).bake(state,format,bakedTextureGetter));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
