package com.qtransfer.mod7e.transfer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class CraftGroup {
    public GeneralStack result;
    public List<GeneralStack> stuff;
    public int craftid;
    public ICraftTree ctree;
    public long uid;
    public int per_result;

    public int getCount(){
        return per_result;
    }

    public FluidStack getFluidStack(){
        FluidStack fs=result.fstack.copy();
        fs.amount=per_result;
        return fs;
    }

    public ItemStack getItemStack(){
        ItemStack is=result.stack.copy();
        is.setCount(per_result);
        return is;
    }

    @Override
    public String toString() {
        StringBuilder stuffs= new StringBuilder();
        for(GeneralStack ish:stuff)
            stuffs.append(ish.toString()).append(",");
        return craftid+","+result.toString()+","+stuffs;
    }
}
