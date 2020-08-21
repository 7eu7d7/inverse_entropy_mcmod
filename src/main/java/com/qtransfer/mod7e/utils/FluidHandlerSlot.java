package com.qtransfer.mod7e.utils;

import com.qtransfer.mod7e.transfer.GeneralStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidHandlerSlot implements IHandlerSolt{
    IFluidHandler handler;
    int slot;
    public FluidHandlerSlot(IFluidHandler handler, int slot){
        this.slot=slot;
        this.handler=handler;
    }
    public GeneralStack extract(int amount){
        FluidStack tmp=getFluid().copy();
        tmp.amount=amount;
        return new GeneralStack(handler.drain(tmp,true));
    }
    /*public FluidStack insert(FluidStack stack){
        return handler.insertItem(slot,stack,false);
    }*/
    public int getCount(){
        return getFluid().amount;
    }
    public FluidStack getFluid(){
        return handler.getTankProperties()[slot].getContents();
    }
}
