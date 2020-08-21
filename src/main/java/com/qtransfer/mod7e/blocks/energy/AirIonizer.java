package com.qtransfer.mod7e.blocks.energy;

import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.blocks.QTransBlock;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class AirIonizer extends QTransBlock{
    public static final IProperty<Boolean> LIT = PropertyBool.create("lit");

    public AirIonizer(){
        super();
        rotateable=false;
        setDefaultState(getBlockState().getBaseState().withProperty(LIT, false));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new AirIonizerEntity();
    }

    /*@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.SOLID;
    }*/

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
    public boolean isFullCube(final IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote) {
            player.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_AIR_IONIZER, world, pos.getX(), pos.getY(), pos.getZ());
            tile.markDirty();
        }
        return true;
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LIT);
    }

    @Override
    public int getLightValue(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return state.getValue(LIT) ? 15 : 0;
    }
}
