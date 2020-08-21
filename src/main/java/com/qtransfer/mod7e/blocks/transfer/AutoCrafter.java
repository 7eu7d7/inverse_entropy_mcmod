package com.qtransfer.mod7e.blocks.transfer;

import com.qtransfer.mod7e.blocks.QTransBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AutoCrafter extends QTransBlock {

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new AutoCrafterEntity();
    }

    /*@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof AutoCrafterEntity && !world.isRemote) {
            player.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_Q_BUFFER, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }*/
}
