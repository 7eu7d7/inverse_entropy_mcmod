package com.qtransfer.mod7e.entity.render;

import com.qtransfer.mod7e.Constant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public abstract class EntityOBJRender<T extends Entity> extends Render<T> {
    IBakedModel bakedModel;

    public EntityOBJRender(RenderManager manager) {
        super(manager);
    }

    public void bulidModel(){
        if (bakedModel == null) {
            try {
                IModel model = ModelLoaderRegistry.getModelOrLogError(getModel(), "qtrans is missing a model. Please report this to the mod authors.");
                bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract ResourceLocation getModel();

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        GlStateManager.translate(x,y,z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 90, 0.0F, 1.0F, 0.0F);

        //System.out.println(entity.posX+","+entity.posY+","+entity.posZ);

        //bindTexture(ROBOT_TEX1);
        bulidModel();

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderModelBrightnessColorQuads(1.0F, 1.0F, 1.0F, 1.0F, bakedModel.getQuads(null,null, 0L));

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();

        super.doRender(entity,x,y,z,entityYaw,partialTicks);
    }

    private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        int i = 0;

        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
        for (int j = listQuads.size(); i < j; ++i)
        {
            BakedQuad bakedquad = listQuads.get(i);
            bufferbuilder.addVertexData(bakedquad.getVertexData());

            if (bakedquad.hasTintIndex())
            {
                bufferbuilder.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
            }
            else
            {
                bufferbuilder.putColorRGB_F4(brightness, brightness, brightness);
            }

            Vec3i vec3i = bakedquad.getFace().getDirectionVec();
            bufferbuilder.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
        }
        tessellator.draw();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

}