package com.qtransfer.mod7e.blocks;

import com.qtransfer.mod7e.utils.UnlistedPropertyFluid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockFluidTank extends QTransBlock {
    public static UnlistedPropertyFluid FLUID_TANK= new UnlistedPropertyFluid("fluid_tank");

    public BlockFluidTank(){
        super(true);
        rotateable=false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new BlockFluidTankEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote) {
            IFluidHandler handler_item= FluidUtil.getFluidHandler(player.getHeldItem(hand));
            if(handler_item!=null){
                IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
                FluidUtil.interactWithFluidHandler(player,hand,handler);
                ((QTransTileEntity) tile).syncToTrackingClients();
                tile.markDirty();
                //world.setBlockState(pos,state);


                //world.not(pos,state,state,4);
                System.out.println("update");
            }
        }

        //world.setBlockState(pos,state);

        return true;
    }

    @Override
    public BlockStateContainer createBlockState() {
        IProperty [] listedProperties = new IProperty[0]; // no listed properties
        IUnlistedProperty [] unlistedProperties = new IUnlistedProperty[] {FLUID_TANK};
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {  // avoid crash in case of mismatch
            IExtendedBlockState retval = (IExtendedBlockState)state;
            retval = retval.withProperty(FLUID_TANK, ((BlockFluidTankEntity)world.getTileEntity(pos)).tank);
            return retval;
        }
        return state;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public void getDrops(final NonNullList<ItemStack> drops, final IBlockAccess world, final BlockPos pos, final IBlockState state, final int fortune) {
        System.out.println(world.getTileEntity(pos));
        final IFluidHandler fluidHandler = world.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,null);
        System.out.println(fluidHandler);
        if (fluidHandler != null) {
            ItemStack stack=new ItemStack(this);
            //stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null).fill(fluidHandler.drain(Integer.MAX_VALUE,true),true);
            //drops.add(stack);
            FluidStack drainable = fluidHandler.drain(1000,false);
            if(drainable==null || drainable.amount<=0){
                drops.add(stack);
                return;
            }
            final FluidActionResult fluidActionResult = FluidUtil.tryFillContainer(stack, fluidHandler, Integer.MAX_VALUE, null, true);
            System.out.println(fluidActionResult.isSuccess());
            if (fluidActionResult.isSuccess()) {
                drops.add(fluidActionResult.getResult());
            }
        }
    }

    @Override
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        final IFluidHandler fluidHandler = worldIn.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,null);
        if (fluidHandler != null) {
            FluidUtil.tryEmptyContainer(stack, fluidHandler, Integer.MAX_VALUE, null, true);
        }
    }
}
