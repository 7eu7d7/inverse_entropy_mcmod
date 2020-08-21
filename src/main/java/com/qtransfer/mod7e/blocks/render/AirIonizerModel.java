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

public class AirIonizerModel implements IModel{

    public static final ResourceLocation TEXTURE_SIDE = new ResourceLocation(Constant.item("blocks/air_ionizer"));

    // return all the textures used by this model (not strictly needed for this example because we load all the subcomponent
    //   models during the bake anyway)
    @Override
    public Collection<ResourceLocation> getTextures() {
        return ImmutableList.copyOf(new ResourceLocation[]{TEXTURE_SIDE});
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

        try {
            return new AirIonizerBakedModel(ModelLoaderRegistry.getMissingModel().bake(state,format,bakedTextureGetter),
                    Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(TEXTURE_SIDE.toString()));
            //ModelLoaderRegistry.getModel(new ModelResourceLocation(Constant.item("models/block/air_ionizer"))).bake(state,format,bakedTextureGetter));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
