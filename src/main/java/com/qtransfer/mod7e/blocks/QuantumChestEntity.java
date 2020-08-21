package com.qtransfer.mod7e.blocks;

import com.qtransfer.mod7e.items.StorageItem;
import com.qtransfer.mod7e.transfer.GeneralStack;
import com.qtransfer.mod7e.transfer.IStorageable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuantumChestEntity extends QTransTileEntity implements IStorageable, IItemHandler, IFluidHandler {

    public ItemStackHandler inventory_storage = new ItemStackHandler(9);
    public StorageItem[] storage=new StorageItem[9];
    HashMap<GeneralStack, Integer> storage_map=new HashMap<GeneralStack, Integer>();

    public boolean change_mark=false;

    public QuantumChestEntity(){
        updateStorage();
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
        return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
        } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this);
        } else {
            return super.getCapability(cap, facing);
        }
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        for(int i=0;i<storage.length;i++) {
            storage[i] = new StorageItem(inventory_storage.getStackInSlot(i));
            if (storage[i].isStorage() && tag.hasKey("storage"+i)) {
                storage[i].deserializeNBT(tag.getCompoundTag("storage"+i));
            }
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        for(int i=0;i<storage.length;i++) {
            if (storage[i]!=null && storage[i].isStorage()) {
                storage[i].saveNBT();
                nbt.setTag("storage"+i, storage[i].serializeNBT());
            }
        }
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        deserializeNBT(compound.getCompoundTag("storage_list"));
        inventory_storage.deserializeNBT(compound.getCompoundTag("inventory"));
        //if(!world.isRemote)
        updateStorage();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("storage_list",serializeNBT());
        compound.setTag("inventory", inventory_storage.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void saveNBT() {
        writeToNBT(new NBTTagCompound());
    }

    @Override
    public HashMap<GeneralStack, Integer> getStorgeMap() {
        /*HashMap<GeneralStack, Integer> stmap=new HashMap<GeneralStack, Integer>();
        for(StorageItem si:storage){
            HashMap<GeneralStack, Integer> itemmap=si.getStorgeMap();
            for(Map.Entry<GeneralStack, Integer> entry:itemmap.entrySet()){
                if(!stmap.containsKey(entry.getKey()))
                    stmap.put(entry.getKey(),0);
                stmap.put(entry.getKey(),stmap.get(entry.getKey())+entry.getValue());
            }
        }*/
        return storage_map;
    }

    public int getMaxSize(){
        int size=0;
        for(StorageItem si:storage){
            size+=si.getMaxSize();
        }
        return size;
    }

    public int getUsedSize(){
        int size=0;
        for(StorageItem si:storage){
            size+=si.getUsedSize();
        }
        return size;
    }

    public void updateStorage(){
        for(int i=0;i<storage.length;i++)
            storage[i]=new StorageItem(inventory_storage.getStackInSlot(i));

        storage_map.clear();
        for(StorageItem si:storage){
            HashMap<GeneralStack, Integer> itemmap=si.getStorgeMap();
            for(Map.Entry<GeneralStack, Integer> entry:itemmap.entrySet()){
                if(!storage_map.containsKey(entry.getKey()))
                    storage_map.put(entry.getKey(),0);
                storage_map.put(entry.getKey(),storage_map.get(entry.getKey())+entry.getValue());
            }
        }
    }

    //ItemHandler
    @Override
    public boolean hasItem(GeneralStack itemlist) {
        return storage_map.containsKey(itemlist);
    }

    @Override
    public int getItemCount(GeneralStack itemlist) {
        return storage_map.get(itemlist);
    }

    @Override
    public GeneralStack addItem(GeneralStack items, boolean doadd) {
        GeneralStack res=items.copy();
        for(StorageItem si:storage){
            res=si.addItem(res, doadd);
        }

        //更新storage map
        if(doadd) {
            if (!storage_map.containsKey(items))
                storage_map.put(items, items.getCount() - res.getCount());
            else
                storage_map.put(items, storage_map.get(items) + items.getCount() - res.getCount());
            change_mark=true;
        }
        return res;
    }

    @Override
    public GeneralStack takeItem(GeneralStack items, int count, boolean dotake) {
        if(!storage_map.containsKey(items))
            return items.getEmpty();

        GeneralStack res=items.copy();
        res.setCount(0);
        for(StorageItem si:storage){
            GeneralStack take=si.takeItem(items,count,dotake);
            count-=take.getCount();
            res.setCount(res.getCount()+take.getCount());
            if(count<=0)
                break;
        }

        //更新storage map
        if(dotake) {
            storage_map.put(items, storage_map.get(items) - res.getCount());
            if (storage_map.get(items) <= 0)
                storage_map.remove(items);
            change_mark=true;
        }
        return res;
    }

    @Override
    public int getSlots() {
        return storage_map.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if(slot>=getSlots())
            return ItemStack.EMPTY;
        GeneralStack[] list=storage_map.keySet().toArray(new GeneralStack[0]);
        if(list[slot].fluid)
            return ItemStack.EMPTY;
        else {
            GeneralStack stack=list[slot].copy();
            stack.setCount(storage_map.get(stack));
            return stack.stack;
        }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return addItem(new GeneralStack(stack), !simulate).stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(slot>=getSlots())
            return ItemStack.EMPTY;
        GeneralStack[] list=storage_map.keySet().toArray(new GeneralStack[0]);
        if(list[slot].fluid)
            return ItemStack.EMPTY;
        else {
            GeneralStack stack=list[slot].copy();
            return takeItem(stack,amount,!simulate).stack;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    //FluidHanlder
    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[0];
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return resource.amount-addItem(new GeneralStack(resource),doFill).getCount();
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return takeItem(new GeneralStack(resource),resource.amount,doDrain).fstack;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        for(GeneralStack stack:storage_map.keySet()){
            if(stack.fluid)
                return takeItem(new GeneralStack(stack.fstack),maxDrain,doDrain).fstack;
        }
        return null;
    }
}
