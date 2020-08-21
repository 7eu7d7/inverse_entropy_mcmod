package com.qtransfer.mod7e.blocks.energy;

import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.blocks.QTransBlock;
import com.qtransfer.mod7e.blocks.transfer.AutoCrafterEntity;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ElectronConstraintor extends QTransBlock{

    public ElectronConstraintor(){
        super();
        enableEnergy();
        energy_init=true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new ElectronConstraintorEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote) {
            player.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_ELECTRON_CONSTRAINTOR, world, pos.getX(), pos.getY(), pos.getZ());
            tile.markDirty();
        }
        return true;
    }
}
