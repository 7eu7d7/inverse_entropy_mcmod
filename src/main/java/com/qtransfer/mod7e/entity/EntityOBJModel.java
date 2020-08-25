package com.qtransfer.mod7e.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

public abstract class EntityOBJModel<T extends Entity> extends Render<T> {

    // Note: Make sure to register new textures in ClientProxy#stitchEventPre
    private IBakedModel[] bakedModels;

    protected EntityOBJModel(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }

    public abstract ResourceLocation[] getEntityModels();
    public abstract boolean preRender(T entity, int model, BufferBuilder buffer, double x, double y, double z, float entityYaw, float partialTicks);
    public IModel retexture(int i, IModel model) {return model;}
    public int getColor(int i, T entity) {return -1;}

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // bake models
        if (this.bakedModels == null) {
            this.bakedModels = new IBakedModel[this.getEntityModels().length];
            for (int i=0; i<this.getEntityModels().length; ++i) {
                IModel model = ModelLoaderRegistry.getModelOrLogError(this.getEntityModels()[i], "Minewatch is missing a model. Please report this to the mod authors.");
                model = this.retexture(i, model);
                bakedModels[i] = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
            }
        }

        for (int i=0; i<this.bakedModels.length; ++i) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.pushMatrix();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

            // rotate/translate
            {
                GlStateManager.rotate(180, 0, 0, 1);
                GlStateManager.translate((float)-x, (float)-y, (float)z);
                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
            }

            if (this.preRender(entity, i, buffer, x, y, z, entityYaw, partialTicks)) {
                int color = this.getColor(i, entity);
                GlStateManager.rotate(-(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks), 1.0F, 0.0F, 0.0F);

                List<BakedQuad> quads = this.bakedModels[i].getQuads(null, null, 0);
                if(!quads.isEmpty()) {
                    for(BakedQuad quad : quads)
                        LightUtil.renderQuadColor(buffer, quad, color == -1 ? color : color | -16777216);
                }
            }
            buffer.setTranslation(0, 0, 0);
            tessellator.draw();

            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        }
    }

}