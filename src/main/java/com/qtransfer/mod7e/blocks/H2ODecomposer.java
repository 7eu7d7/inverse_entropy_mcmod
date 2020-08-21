package com.qtransfer.mod7e.blocks;

import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public final class H2ODecomposer extends QTransBlock {

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new H2ODecomposerEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof H2ODecomposerEntity && !world.isRemote) {
            FluidStack fs=FluidUtil.getFluidContained(player.getHeldItem(hand));
            if(fs!=null && fs.getFluid()== FluidRegistry.WATER){
                IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
                FluidUtil.interactWithFluidHandler(player,hand,handler);
                ((H2ODecomposerEntity) tile).syncToTrackingClients();
                tile.markDirty();
                //world.notifyBlockUpdate(pos,state,state,3);
            } else {
                ((H2ODecomposerEntity) tile).syncToTrackingClients();
                //world.notifyBlockUpdate(pos,state,state,3);
                player.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_H2O_DECOMP, world, pos.getX(), pos.getY(), pos.getZ());
                tile.markDirty();
            }
        }
        return true;
    }

}
