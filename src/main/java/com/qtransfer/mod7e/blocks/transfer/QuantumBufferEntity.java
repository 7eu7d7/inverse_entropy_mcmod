package com.qtransfer.mod7e.blocks.transfer;

import com.qtransfer.mod7e.blocks.QTransTileEntity;
import com.qtransfer.mod7e.items.StorageItem;
import com.qtransfer.mod7e.transfer.GeneralStack;
import com.qtransfer.mod7e.transfer.IQuantumNetwork;
import com.qtransfer.mod7e.transfer.IStorageable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashMap;

public class QuantumBufferEntity extends QTransTileEntity implements IStorageable,IQuantumNetwork {
    public ItemStackHandler inventory_storage = new ItemStackHandler(1);
    public StorageItem storage=new StorageItem();

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        storage.deserializeNBT(tag);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return storage.serializeNBT();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        storage=new StorageItem(inventory_storage.getStackInSlot(0));
        if(storage.isStorage()) {
            deserializeNBT(compound.getCompoundTag("storage"));
        }
        inventory_storage.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if(storage.isStorage()) {
            storage.saveNBT();
            compound.setTag("storage", storage.serializeNBT());
        }
        compound.setTag("inventory", inventory_storage.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void saveNBT() {
        writeToNBT(new NBTTagCompound());
    }

    @Override
    public HashMap<GeneralStack, Integer> getStorgeMap() {
        return storage.getStorgeMap();
    }

    public int getMaxSize(){
        return storage.getMaxSize();
    }

    public int getUsedSize(){
        return storage.getUsedSize();
    }

    @Override
    public boolean hasItem(GeneralStack itemlist) {
        return storage.hasItem(itemlist);
    }

    @Override
    public int getItemCount(GeneralStack itemlist) {
        return storage==null?0:storage.getItemCount(itemlist);
    }

    @Override
    public GeneralStack addItem(GeneralStack items, boolean doadd) {
        GeneralStack res=storage.addItem(items, doadd);
        //syncToTrackingClients();
        return res;
    }

    @Override
    public GeneralStack takeItem(GeneralStack items, int count, boolean dotake) {
        GeneralStack res=storage.takeItem(items,count,dotake);
        //syncToTrackingClients();
        return res;
    }

    @Override
    public void updateNetwork(){
        if(StorageItem.isStorage(inventory_storage.getStackInSlot(0))) {
            setStorage(inventory_storage.getStackInSlot(0));
        }
    }

    public void setStorage(ItemStack item_st){
        storage=new StorageItem(item_st);
        //syncToTrackingClients();
    }
}
