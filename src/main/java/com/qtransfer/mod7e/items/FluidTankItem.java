package com.qtransfer.mod7e.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidTankItem implements IFluidHandlerItem, INBTSerializable<NBTTagCompound> {
    ItemStack stack;
    public FluidTank tank=new FluidTank(64000);

    public FluidTankItem(ItemStack stack){
        this.stack=stack;
        if(stack.hasTagCompound()){
            deserializeNBT(stack.getTagCompound());
        }
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return tank.getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        int res=tank.fill(resource,doFill);
        stack.setTagCompound(serializeNBT());
        return res;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        FluidStack res=tank.drain(resource,doDrain);
        stack.setTagCompound(serializeNBT());
        return res;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack res=tank.drain(maxDrain,doDrain);
        stack.setTagCompound(serializeNBT());
        return res;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return tank.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        tank.readFromNBT(nbt);
    }

    @Nonnull
    @Override
    public ItemStack getContainer() {
        return stack;
    }
}
