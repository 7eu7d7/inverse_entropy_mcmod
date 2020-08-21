package com.qtransfer.mod7e.transfer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;

public interface IStorageable extends INBTSerializable<NBTTagCompound> {
    boolean hasItem(GeneralStack itemlist);
    int getItemCount(GeneralStack itemlist);
    GeneralStack addItem(GeneralStack items, boolean doadd); //返回剩余量
    GeneralStack takeItem(GeneralStack items, int count, boolean dotake);
    void saveNBT();
    HashMap<GeneralStack, Integer> getStorgeMap();
    /*void readFromNBT(NBTTagCompound tag);
    NBTTagCompound writeToNBT(NBTTagCompound tag);*/

    int getMaxSize();
    int getUsedSize();
}
