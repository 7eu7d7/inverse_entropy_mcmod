package com.qtransfer.mod7e.blocks.render;

import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.blocks.BlockFluidTankEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.vecmath.Vector3f;

public class FluidTankRender extends FastTESR<BlockFluidTankEntity> {
    //private static final BlockModelRenderer renderer = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer();

    @Override
    public void renderTileEntityFast(BlockFluidTankEntity te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {
        GlStateManager.pushMatrix();
        int capacity = te.tank.getCapacity();
        FluidStack fluid = te.tank.getFluid();
        if (fluid != null)
        {
            buffer.setTranslation(x, y, z);

            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill().toString());
            TextureAtlasSprite flow =  Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getFlowing().toString());

            double posY = .1 + (.8 * ((float) fluid.amount / (float) capacity));
            float[] color = Utils.color2arr(fluid.getFluid().getColor(fluid));

            double sx=0.5,sz=0.5,ex=15.5,ez=15.5;
            double bottom=0.5;
            int skyLight=240,blockLight=0;

            //top
            buffer.pos( sx/16, posY,  sz/16).color(color[0], color[1], color[2], color[3])
                    .tex(still.getInterpolatedU( sx), still.getInterpolatedV( sz)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos(ex/16, posY,  sz/16).color(color[0], color[1], color[2], color[3])
                    .tex(still.getInterpolatedU(ex), still.getInterpolatedV( sz)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos(ex/16, posY, ez/16).color(color[0], color[1], color[2], color[3])
                    .tex(still.getInterpolatedU(ex), still.getInterpolatedV(ez)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos( sx/16, posY, ez/16).color(color[0], color[1], color[2], color[3])
                    .tex(still.getInterpolatedU( sx), still.getInterpolatedV(ez)).lightmap(skyLight,blockLight).endVertex();

            //back
            buffer.pos(ex/16, bottom/16, ez/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU(ex), flow.getInterpolatedV(15)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos(ex/16, posY, ez/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU(ex), flow.getInterpolatedV( 1)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos( sx/16, posY, ez/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU( sx), flow.getInterpolatedV( 1)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos( sx/16, bottom/16, ez/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU( sx), flow.getInterpolatedV(15)).lightmap(skyLight,blockLight).endVertex();

            //right
            buffer.pos(ex/16, bottom/16, sz/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU(ex), flow.getInterpolatedV(15)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos(ex/16, posY, sz/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU(ex), flow.getInterpolatedV( 1)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos( ex/16, posY, ez/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU( sx), flow.getInterpolatedV( 1)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos( ex/16, bottom/16, ez/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU( sx), flow.getInterpolatedV(15)).lightmap(skyLight,blockLight).endVertex();

            //front
            buffer.pos(sx/16, bottom/16, sz/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU(ex), flow.getInterpolatedV(15)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos(sx/16, posY, sz/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU(ex), flow.getInterpolatedV( 1)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos( ex/16, posY, sz/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU( sx), flow.getInterpolatedV( 1)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos( ex/16, bottom/16, sz/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU( sx), flow.getInterpolatedV(15)).lightmap(skyLight,blockLight).endVertex();

            //left
            buffer.pos(sx/16, bottom/16, ez/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU(ex), flow.getInterpolatedV(15)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos(sx/16, posY, ez/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU(ex), flow.getInterpolatedV( 1)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos( sx/16, posY, sz/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU( sx), flow.getInterpolatedV( 1)).lightmap(skyLight,blockLight).endVertex();
            buffer.pos( sx/16, bottom/16, sz/16).color(color[0], color[1], color[2], color[3])
                    .tex(flow.getInterpolatedU( sx), flow.getInterpolatedV(15)).lightmap(skyLight,blockLight).endVertex();

            buffer.setTranslation(-x, -y, -z);
        }
        GlStateManager.popMatrix();
    }



}
