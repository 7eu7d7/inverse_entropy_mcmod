package com.qtransfer.mod7e.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class CraftItemHandler extends ItemStackHandler{

    public CraftItemHandler()
    {
        stacks=new NonNullArrayList<ItemStack>(new ArrayList<ItemStack>(),ItemStack.EMPTY,1);
    }

    public CraftItemHandler(int n)
    {
        stacks=new NonNullArrayList<ItemStack>(new ArrayList<ItemStack>(),ItemStack.EMPTY,n);
    }

    /*@Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if(simulate){
            super.insertItem(slot, stack, false);
            return stack.copy();
        } else {
            //super.insertItem(slot, stack, simulate);

            return stack.copy();
        }
    }*/

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(simulate){
            super.extractItem(slot, amount, false);
            return ItemStack.EMPTY;
        } else {
            //super.extractItem(slot, amount, simulate);
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        super.setStackInSlot(slot, stack);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < stacks.size(); i++)
        {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setInteger("Slot", i);
            stacks.get(i).writeToNBT(itemTag);
            nbtTagList.appendTag(itemTag);
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Items", nbtTagList);
        nbt.setInteger("Size", stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        int size=nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.size();
        setSize(size);
        NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");

            if (slot >= 0 && slot < size)
            {
                stacks.set(slot,new ItemStack(itemTags));
            }
        }
        onLoad();
    }

    @Override
    public void setSize(int size) {
        stacks=new NonNullArrayList<ItemStack>(new ArrayList<ItemStack>(),ItemStack.EMPTY,size);
    }

    public int addStuffSolt(){
        stacks.add(ItemStack.EMPTY);
        return stacks.size()-1;
    }

}

