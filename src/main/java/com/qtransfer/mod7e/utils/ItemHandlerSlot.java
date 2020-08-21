package com.qtransfer.mod7e.utils;

import com.qtransfer.mod7e.transfer.GeneralStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerSlot implements IHandlerSolt{
    IItemHandler handler;
    int slot;
    public ItemHandlerSlot(IItemHandler handler, int slot){
        this.slot=slot;
        this.handler=handler;
    }
    public GeneralStack extract(int amount){
        return new GeneralStack(handler.extractItem(slot,amount,false));
    }
    public ItemStack insert(ItemStack stack){
        return handler.insertItem(slot,stack,false);
    }
    public int getCount(){
        return handler.getStackInSlot(slot).getCount();
    }
}
