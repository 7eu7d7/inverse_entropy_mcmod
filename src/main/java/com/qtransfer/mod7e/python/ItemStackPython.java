package com.qtransfer.mod7e.python;

import net.minecraft.item.ItemStack;

public class ItemStackPython {
    public ItemStack stack;

    public ItemStackPython(ItemStack stack){
        this.stack=stack;
    }

    public int getCount(){
        return stack.getCount();
    }

    public int getMetadata(){
        return stack.getMetadata();
    }

    public String getName(){
        return stack.getItem().getRegistryName().toString();
    }

    public boolean isEmpty(){
        return stack.isEmpty();
    }

    protected void set(ItemStack stack){
        this.stack=stack;
    }
}
