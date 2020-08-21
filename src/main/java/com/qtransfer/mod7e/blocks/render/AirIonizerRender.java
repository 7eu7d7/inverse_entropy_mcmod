package com.qtransfer.mod7e.blocks.render;

import com.qtransfer.mod7e.Constant;
import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.blocks.BlockFluidTankEntity;
import com.qtransfer.mod7e.blocks.energy.AirIonizerEntity;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.awt.*;

public class AirIonizerRender extends TileEntitySpecialRenderer<AirIonizerEntity> {
    //private static final BlockModelRenderer renderer = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();
    //public int tick=0;
    public float[] roty=new float[3],rotz=new float[3];

    @Override
    public void render(AirIonizerEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        if (te.canWork()>0)
        {
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            //GlStateManager.translate((float)x, (float)y, (float)z);

            for(int i=0;i<roty.length;i++) {
                roty[i] = (float) (Math.random() * 360);
                rotz[i] = -((float) (Math.random() * 110) - 10);
            }

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            //buffer.setTranslation(x, y, z);

            GlStateManager.translate((float)x, (float)y, (float)z);
            //rotateZ(45,0.5f,0,0.5f);
            //rotateY(45,0.5f,0,0.5f);

            bindTexture(Constant.TEXTURE_LIGHTING);

            double sx=0.5,sz=0.5,ex=15.5,ez=15.5;
            int skyLight=240,blockLight=0;
            float[] color = Utils.color2arr(0xffffffff);

            Point3f center=new Point3f(0.5f,1f,0.5f);
            Point3f pdia=new Point3f(-0.5f,2f,0.5f);
            Point3f p2=new Point3f(-0.5f,1f,0.5f);
            Point3f p3=new Point3f(0.5f,2f,0.5f);

            for(int i=0;i<roty.length;i++) {

                GlStateManager.pushMatrix();
                GlStateManager.translate(0.5f, 1, 0.5f);
                GlStateManager.rotate(roty[i], 0, 1, 0);
                GlStateManager.rotate(rotz[i], 0, 0, 1);
                GlStateManager.translate(-0.5f, -1, -0.5f);

                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                buffer.pos(center.x, center.y, center.z)
                        .tex(0, 0).color(color[0], color[1], color[2], color[3]).endVertex();
                buffer.pos(p2.x, p2.y, p2.z)
                        .tex(1, 0).color(color[0], color[1], color[2], color[3]).endVertex();
                buffer.pos(pdia.x, pdia.y, pdia.z)
                        .tex(1, 1).color(color[0], color[1], color[2], color[3]).endVertex();
                buffer.pos(p3.x, p3.y, p3.z)
                        .tex(0, 1).color(color[0], color[1], color[2], color[3]).endVertex();

                tessellator.draw();
                GlStateManager.popMatrix();
            }
            //buffer.setTranslation(0, 0, 0);

            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            RenderHelper.enableStandardItemLighting();
        }
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(AirIonizerEntity tile) { // 实际上是泛型参数
        // 这个方法用于决定是否渲染类似结构方块一样尺寸巨大，甚至跨区块的内容。
        // 默认为 false，若返回 false，那么这个 TESR 只有在处于玩家视锥（Frustum）覆盖区块内时才会渲染。
        return true;
    }

    public void rotateX(float angle,float cx,float cy,float cz){
        GlStateManager.translate(cx,cy,cz);
        GlStateManager.rotate(angle,1,0,0);
        GlStateManager.translate(-cx,-cy,-cz);
    }

    public void rotateY(float angle,float cx,float cy,float cz){
        GlStateManager.translate(cx,cy,cz);
        GlStateManager.rotate(angle,0,1,0);
        GlStateManager.translate(-cx,-cy,-cz);
    }

    public void rotateZ(float angle,float cx,float cy,float cz){
        GlStateManager.translate(cx,cy,cz);
        GlStateManager.rotate(angle,0,0,1);
        GlStateManager.translate(-cx,-cy,-cz);
    }
}
