package com.qtransfer.mod7e.entity;

import com.qtransfer.mod7e.Constant;
import com.qtransfer.mod7e.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class QRobotRenderer extends Render<QRobotEntity> {
    public static final ResourceLocation ROBOT_TEX1=new ResourceLocation(Constant.item("textures/entity/eye_smile.png"));
    public final ResourceLocation RES_MODEL=new ResourceLocation(Constant.MODID, "entity/qrobot_opt2.obj");
    IBakedModel bakedModel;


    public QRobotRenderer(RenderManager manager) {
        super(manager);
    }

    public void bulidModel(){
        if (bakedModel == null) {

            try {
                IModel model = ModelLoaderRegistry.getModelOrLogError(RES_MODEL, "qtrans is missing a model. Please report this to the mod authors.");
                bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, new DefaultTextureGetter());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {

        public TextureAtlasSprite apply(ResourceLocation location) {
            System.out.println("robot:"+location.toString());
            //QRobotRenderer.this.bindTexture(location);
            /*if(location.toString().startsWith("qtrans"))
                textures.add(location);*/

            TextureAtlasSprite tas=Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
            System.out.println(tas);
            return tas;
        }
    }

    @Override
    public void doRender(QRobotEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(x,y,z);
        //GlStateManager.translate(entity.posX, entity.posY, entity.posZ);
        //GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        //System.out.println(entity.getPosition());
        //System.out.println(x+","+y+","+z);


        //bindTexture(ROBOT_TEX1);
        bulidModel();

        /*Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer()
                .renderModelBrightnessColor(bakedModel, 1.0F, 1.0F, 1.0F, 1.0F);*/
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        renderModelBrightnessColorQuads(1.0F, 1.0F, 1.0F, 1.0F, bakedModel.getQuads(null,null, 0L));

        /*bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.pos(0, 0, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 0).lightmap(240, 0).endVertex();
        buffer.pos(0, 10, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 1).lightmap(240, 0).endVertex();
        buffer.pos(10, 10, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 1).lightmap(240, 0).endVertex();
        buffer.pos(10, 0, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 0).lightmap(240, 0).endVertex();
        tessellator.draw();*/


        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
        super.doRender(entity,x,y,z,entityYaw,partialTicks);
    }

    private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        int i = 0;

        //System.out.println("qsize:"+listQuads.size());

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
    protected ResourceLocation getEntityTexture(QRobotEntity entity) {
        /*TextureAtlasSprite tas=Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(ROBOT_TEX1);
        System.out.println("load textures");*/
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

}
