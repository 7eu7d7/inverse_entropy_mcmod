package com.qtransfer.mod7e.blocks.render;

import com.google.common.primitives.Ints;
import com.qtransfer.mod7e.Constant;
import com.qtransfer.mod7e.blocks.BlockFluidTank;
import com.qtransfer.mod7e.items.FluidTankItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class FluidTankBakedModel extends BaseBakedModel{

    public static final ModelResourceLocation variantTag
            = new ModelResourceLocation(Constant.item("fluid_tank_model"), "normal");
    TextureAtlasSprite texture;
    FluidTank render_tank;
    IBakedModel tank_model;

    public FluidTankBakedModel(IBakedModel parent,IBakedModel tank,TextureAtlasSprite texture) {
        super(parent);
        this.texture=texture;
        sub_models.add(this.tank_model=tank);
    }
    public FluidTankBakedModel(IBakedModel parent,IBakedModel tank,TextureAtlasSprite texture,FluidTank render_tank) {
        this(parent,tank,texture);
        this.render_tank=render_tank;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        quad_list.clear();
        make_fluid_quads(state);
        return super.getQuads(state, side, rand);
    }

    private void make_fluid_quads(IBlockState state){
        float x1, x2, x3, x4;
        float y1, y2, y3, y4;
        float z1, z2, z3, z4;
        int packednormal;

        float pmax=0.96f,pmin=0.04f;

        FluidTank tank;
        if(render_tank==null) {
            IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
            tank = extendedBlockState.getValue(BlockFluidTank.FLUID_TANK);
        } else {
            tank=render_tank;
        }
        if(tank.getFluidAmount()<=0)
            return;

        TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(tank.getFluid().getFluid().getStill().toString());
        float rate=(float) tank.getFluidAmount()/tank.getCapacity();

        x1=pmin; y1=rate; z1=pmin;
        x2=pmin; y2=pmin; z2=pmin;
        x3=pmin; y3=pmin; z3=pmax;
        x4=pmin; y4=rate; z4=pmax;

        packednormal=calculatePackedNormal(x1,y1,z1,x2,y2,z2,x3,y3,z3);
        addQuad(new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), still, 0, 0,packednormal),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), still, 0, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), still, 16, 16,packednormal),
                vertexToInts(x4, y4, z4, Color.WHITE.getRGB(), still, 16, 0,packednormal)),
                0, EnumFacing.SOUTH, still, true, DefaultVertexFormats.ITEM));

        x1=pmax; y1=rate; z1=pmax;
        x2=pmax; y2=pmin; z2=pmax;
        x3=pmax; y3=pmin; z3=pmin;
        x4=pmax; y4=rate; z4=pmin;

        packednormal=calculatePackedNormal(x1,y1,z1,x2,y2,z2,x3,y3,z3);
        addQuad(new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), still, 0, 0,packednormal),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), still, 0, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), still, 16, 16,packednormal),
                vertexToInts(x4, y4, z4, Color.WHITE.getRGB(), still, 16, 0,packednormal)),
                0, EnumFacing.NORTH, still, true, DefaultVertexFormats.ITEM));

        x1=pmax; y1=rate; z1=pmin;
        x2=pmax; y2=pmin; z2=pmin;
        x3=pmin; y3=pmin; z3=pmin;
        x4=pmin; y4=rate; z4=pmin;

        packednormal=calculatePackedNormal(x1,y1,z1,x2,y2,z2,x3,y3,z3);
        addQuad(new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), still, 0, 0,packednormal),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), still, 0, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), still, 16, 16,packednormal),
                vertexToInts(x4, y4, z4, Color.WHITE.getRGB(), still, 16, 0,packednormal)),
                0, EnumFacing.WEST, still, true, DefaultVertexFormats.ITEM));

        x1=pmin; y1=rate; z1=pmax;
        x2=pmin; y2=pmin; z2=pmax;
        x3=pmax; y3=pmin; z3=pmax;
        x4=pmax; y4=rate; z4=pmax;

        packednormal=calculatePackedNormal(x1,y1,z1,x2,y2,z2,x3,y3,z3);
        addQuad(new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), still, 0, 0,packednormal),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), still, 0, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), still, 16, 16,packednormal),
                vertexToInts(x4, y4, z4, Color.WHITE.getRGB(), still, 16, 0,packednormal)),
                0, EnumFacing.EAST, still, true, DefaultVertexFormats.ITEM));

        x1=pmax; y1=rate; z1=pmin;
        x2=pmin; y2=rate; z2=pmin;
        x3=pmin; y3=rate; z3=pmax;
        x4=pmax; y4=rate; z4=pmax;

        packednormal=calculatePackedNormal(x1,y1,z1,x2,y2,z2,x3,y3,z3);
        addQuad(new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), still, 0, 0,packednormal),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), still, 0, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), still, 16, 16,packednormal),
                vertexToInts(x4, y4, z4, Color.WHITE.getRGB(), still, 16, 0,packednormal)),
                0, EnumFacing.UP, still, true, DefaultVertexFormats.ITEM));
        x1=pmin; y1=pmin; z1=pmin;
        x2=pmax; y2=pmin; z2=pmin;
        x3=pmax; y3=pmin; z3=pmax;
        x4=pmin; y4=pmin; z4=pmax;

        packednormal=calculatePackedNormal(x1,y1,z1,x2,y2,z2,x3,y3,z3);
        addQuad(new BakedQuad(Ints.concat(vertexToInts(x1, y1, z1, Color.WHITE.getRGB(), still, 0, 0,packednormal),
                vertexToInts(x2, y2, z2, Color.WHITE.getRGB(), still, 0, 16,packednormal),
                vertexToInts(x3, y3, z3, Color.WHITE.getRGB(), still, 16, 16,packednormal),
                vertexToInts(x4, y4, z4, Color.WHITE.getRGB(), still, 16, 0,packednormal)),
                0, EnumFacing.DOWN, still, true, DefaultVertexFormats.ITEM));
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return texture;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(Collections.EMPTY_LIST){
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return new FluidTankBakedModel(originalModel,tank_model,texture,
                        ((FluidTankItem)stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null)).tank);
            }
        };
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }
}
