package com.qtransfer.mod7e.blocks.energy;

import com.qtransfer.mod7e.Constant;
import com.qtransfer.mod7e.blocks.QTransTileEntity;
import com.qtransfer.mod7e.energy.QEnergyProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class AirIonizerEntity extends QTransTileEntity{
    public ItemStackHandler inventory = new ItemStackHandler(1);

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.deserializeNBT(tag.getCompoundTag("inventory"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("inventory",inventory.serializeNBT());
        return super.writeToNBT(tag);
    }

    public int canWork(){
        if(inventory.getStackInSlot(0).isEmpty())
            return 0;
        else if(inventory.getStackInSlot(0).getItem().getRegistryName().toString().equals(Constant.item("hse_ball")))
            return 2000;
        else if(inventory.getStackInSlot(0).getItem().getRegistryName().toString().equals(Constant.item("dense_electron_ball")))
            return 5000;
        else
            return 0;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return (oldState.getBlock() != newState.getBlock());
    }
}
