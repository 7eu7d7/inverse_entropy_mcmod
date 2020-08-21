package com.qtransfer.mod7e.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;

public class StoragePluginItem implements INBTSerializable<NBTTagCompound> {
    public EnumFacing face=EnumFacing.DOWN;
    ItemStack storage;

    public StoragePluginItem(ItemStack stack){
        storage=stack;
        deserializeNBT(stack.getTagCompound());
    }

    public void writeNBT(){
        storage.setTagCompound(serializeNBT());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        nbt.setInteger("face",face.getIndex());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt==null)
            return;
        face=EnumFacing.VALUES[nbt.getInteger("face")];
    }

    public void setFace(EnumFacing face){
        this.face=face;
    }
}
