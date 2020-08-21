package com.qtransfer.mod7e.python;

import net.minecraftforge.fluids.FluidStack;

public class FluidStackPython {
    public FluidStack stack;

    public FluidStackPython(FluidStack stack){
        this.stack=stack;
    }

    public int getCount(){
        return stack.amount;
    }

    public String getName(){
        return stack.getFluid().getName();
    }

    public boolean isEmpty(){
        return stack==null || stack.amount<=0;
    }
}
