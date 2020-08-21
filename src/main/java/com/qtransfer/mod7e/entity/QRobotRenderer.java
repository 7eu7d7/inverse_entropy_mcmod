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
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class QRobotRenderer extends Render<QRobotEntity> {
    public static final ResourceLocation ROBOT_TEX=new ResourceLocation(Constant.item("textures/entity/qrobot.png"));
    public final ResourceLocation RES_MODEL=new ResourceLocation(Constant.MODID, "models/entity/qrobot.obj");
    IBakedModel bakedModel;

    public QRobotRenderer(RenderManager manager) {
        super(manager);
        bulidModel();
    }

    public void bulidModel(){
        if (bakedModel == null) {
            try {
                IModel model = OBJLoader.INSTANCE.loadModel(RES_MODEL);
                bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, DefaultTextureGetter.INSTANCE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private enum DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {
        INSTANCE;

        public TextureAtlasSprite apply(ResourceLocation location) {
//            return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
            System.out.println("robot:"+location.toString());
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
        System.out.println(entity.getPosition());
        System.out.println(x+","+y+","+z);

        bulidModel();

        /*Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        BlockPos blockPos = new BlockPos(0,0,0);
        IBlockState blockState = entity.world.getBlockState(blockPos);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);*/

        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer()
                .renderModelBrightnessColor(bakedModel, 1.0F, 1.0F, 1.0F, 1.0F);
        /*Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
                entity.world,
                bakedModel,
                blockState,
                blockPos,
                buffer, false, 0);*/

        /*
        buffer.pos(0, 0, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 0).lightmap(240, 0).endVertex();
        buffer.pos(0, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 1).lightmap(240, 0).endVertex();
        buffer.pos(1, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 1).lightmap(240, 0).endVertex();
        buffer.pos(1, 0, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 0).lightmap(240, 0).endVertex();

        tessellator.draw();
        */

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
        super.doRender(entity,x,y,z,entityYaw,partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(QRobotEntity entity) {
        return ROBOT_TEX;
        //return RES_MODEL;
    }

}
