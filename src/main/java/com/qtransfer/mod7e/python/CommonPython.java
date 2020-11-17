package com.qtransfer.mod7e.python;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CommonPython {

    public List<ItemStackPython> items(List<ItemStack> stacks){
        List<ItemStackPython> sps=new ArrayList<ItemStackPython>();
        for(ItemStack stack:stacks)
            sps.add(new ItemStackPython(stack));
        return sps;
    }

    public ItemStackPython item(ItemStack stack){
        return new ItemStackPython(stack);
    }

    public int check_meta(String name){
        int st=name.indexOf(":");
        return name.indexOf(":",st+1);
    }
}
