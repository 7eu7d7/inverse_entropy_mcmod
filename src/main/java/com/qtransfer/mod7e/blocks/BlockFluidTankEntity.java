package com.qtransfer.mod7e.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class BlockFluidTankEntity extends QTransTileEntity {
    public FluidTank tank=new FluidTank(64000);

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.tank.readFromNBT(tag.getCompoundTag("tank"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("tank", this.tank.writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(tag);
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
        return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(cap, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
        } else {
            return super.getCapability(cap, facing);
        }
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        world.markBlockRangeForRenderUpdate(pos,pos.add(1,1,1));
    }

    /*@Override
    public boolean hasFastRenderer() {
        return true;
    }*/
}
