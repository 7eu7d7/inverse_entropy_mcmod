package com.qtransfer.mod7e.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class IOItemStackHandler implements IItemHandler {
    int input_range;
    ItemStackHandler handler;

    public IOItemStackHandler(ItemStackHandler handler,int input_range)
    {
        this.handler=handler;
        this.input_range=input_range;
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(slot<input_range)
            return handler.insertItem(slot, stack, simulate);
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(slot>=input_range)
            return handler.extractItem(slot, amount, simulate);
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

}
