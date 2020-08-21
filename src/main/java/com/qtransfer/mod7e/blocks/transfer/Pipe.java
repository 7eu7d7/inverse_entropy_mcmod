package com.qtransfer.mod7e.blocks.transfer;

import com.google.common.collect.ImmutableList;
import com.qtransfer.mod7e.blocks.QTransBlock;
import com.qtransfer.mod7e.transfer.IQuantumNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pipe extends Block {

    public static final float PIPE_MIN_POS = 0.25f;
    public static final float PIPE_MAX_POS = 0.75f;

    public static final ImmutableList<AxisAlignedBB> CONNECTED_BOUNDING_BOXES = ImmutableList.copyOf(
            Stream.of(EnumFacing.VALUES)
                    .map(facing -> {
                        Vec3i directionVec = facing.getDirectionVec();
                        return new AxisAlignedBB(
                                getMinBound(directionVec.getX()), getMinBound(directionVec.getY()), getMinBound(directionVec.getZ()),
                                getMaxBound(directionVec.getX()), getMaxBound(directionVec.getY()), getMaxBound(directionVec.getZ())
                        );
                    })
                    .collect(Collectors.toList())
    );

    public Pipe() {
        super(Material.IRON);
        setHardness(0.6F);
        setHarvestLevel("pickaxe", 0);
        setResistance(1000);
    }

    private static float getMinBound(final int dir) {
        return dir == -1 ? 0 : PIPE_MIN_POS;
    }

    private static float getMaxBound(final int dir) {
        return dir == 1 ? 1 : PIPE_MAX_POS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, final IBlockAccess world, final BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            state = state.withProperty(PropertyBool.create(facing.name().toLowerCase()), canConnectTo(state, world, pos, facing));
        }
        //System.out.println("get state");
        return state;
    }

    @Override
    public BlockStateContainer createBlockState() {
        PropertyBool[] pros=new PropertyBool[6];
        int i=0;
        for (EnumFacing facing : EnumFacing.VALUES) {
            pros[i++]=PropertyBool.create(facing.name().toLowerCase());
        }
        return new BlockStateContainer(this, pros);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(final IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(final IBlockState state) {
        return false;
    }

    public final boolean isConnected(final IBlockState state, final EnumFacing facing) {
        return state.getValue(PropertyBool.create(facing.name().toLowerCase()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(PIPE_MIN_POS, PIPE_MIN_POS, PIPE_MIN_POS, PIPE_MAX_POS, PIPE_MAX_POS, PIPE_MAX_POS);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        /*final AxisAlignedBB bb = new AxisAlignedBB(PIPE_MIN_POS, PIPE_MIN_POS, PIPE_MIN_POS, PIPE_MAX_POS, PIPE_MAX_POS, PIPE_MAX_POS);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, bb);*/

        if (!isActualState) {
            state = state.getActualState(worldIn, pos);
        }

        for (final EnumFacing facing : EnumFacing.VALUES) {
            if (isConnected(state, facing)) {
                final AxisAlignedBB axisAlignedBB = CONNECTED_BOUNDING_BOXES.get(facing.getIndex());
                addCollisionBoxToList(pos, entityBox, collidingBoxes, axisAlignedBB);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        return 0;
    }

    /*@Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        System.out.println("changed");
    }*/

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        QTransBlock.updateQuantumNet(worldIn, pos);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(worldIn, pos, state, player);

        for(EnumFacing face:EnumFacing.VALUES){
            if(worldIn.getBlockState(pos.offset(face)).getBlock() instanceof Pipe){
                QTransBlock.updateQuantumNet(worldIn,pos.offset(face));
            }
        }
    }

    public boolean canConnectTo(final IBlockState ownState, final IBlockAccess worldIn, final BlockPos ownPos, final EnumFacing neighbourDirection) {
        final BlockPos neighbourPos = ownPos.offset(neighbourDirection);
        final IBlockState neighbourState = worldIn.getBlockState(neighbourPos);
        final Block neighbourBlock = neighbourState.getBlock();

        if(neighbourBlock instanceof Pipe )
            return true;
        else {
            if(neighbourBlock.hasTileEntity(neighbourState)) {
                final TileEntity neighbourEntity = worldIn.getTileEntity(neighbourPos);
                return neighbourEntity instanceof IQuantumNetwork;/*||
                        neighbourEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, neighbourDirection.getOpposite()) ||
                        neighbourEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, neighbourDirection.getOpposite());*/
            }
        }
        return false;
    }

}
