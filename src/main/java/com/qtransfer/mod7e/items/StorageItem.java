package com.qtransfer.mod7e.items;

import com.qtransfer.mod7e.transfer.GeneralStack;
import com.qtransfer.mod7e.transfer.IStorageable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class StorageItem implements IStorageable {
    public static HashMap<String, Integer> STORAGE_MAP=new HashMap<String, Integer>(){{
        put("qtrans:quantum_ball", 1<<13);
        put("qtrans:high_dim_fragment", 1<<16);
    }};

    public ItemStack stack;
    HashMap<GeneralStack, Integer> items=new HashMap<GeneralStack, Integer>(); //物品1个=1  流体1000ml=1
    int maxsize=0;

    public StorageItem(){
        stack=ItemStack.EMPTY;
    }

    public StorageItem(ItemStack stack){
        this.stack=stack;
        deserializeNBT(stack.getTagCompound());
    }

    public static boolean isStorage(ItemStack stack){
        String name=stack.getItem().getRegistryName().toString();
        //System.out.println("stname "+name);
        //System.out.println("regname "+stack.getItem().getRegistryName());
        return STORAGE_MAP.containsKey(name);
    }

    public void init(){
        if(stack!=null && !stack.isEmpty()) {
            String name = stack.getItem().getRegistryName().toString();
            maxsize = STORAGE_MAP.get(name);
        }
    }

    public boolean isStorage(){
        return isStorage(stack);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt){
        if(nbt==null) {
            init();
            return;
        }
        NBTTagList items_nbt=nbt.getTagList("items",10);
        for(int i=0;i<items_nbt.tagCount();i++){
            NBTTagCompound st_item=items_nbt.getCompoundTagAt(i);
            if(st_item.getBoolean("is_fluid")){
                items.put(new GeneralStack(FluidStack.loadFluidStackFromNBT(st_item.getCompoundTag("fluid"))), st_item.getInteger("count"));
            } else {
                items.put(new GeneralStack(new ItemStack(st_item.getCompoundTag("item"))), st_item.getInteger("count"));
            }
        }
        maxsize=nbt.getInteger("max_size");
    }

    @Override
    public NBTTagCompound serializeNBT(){
        NBTTagCompound nbt=new NBTTagCompound();
        NBTTagList items_nbt=new NBTTagList();
        for (Map.Entry<GeneralStack, Integer> entry : items.entrySet()) {
            NBTTagCompound item=new NBTTagCompound();

            GeneralStack ish=entry.getKey();
            item.setBoolean("is_fluid",ish.fluid);

            if(ish.fluid){
                item.setTag("fluid", ish.fstack.writeToNBT(new NBTTagCompound()));
                item.setInteger("count", entry.getValue());
                items_nbt.appendTag(item);
            } else {
                item.setTag("item", ish.stack.serializeNBT());
                item.setInteger("count", entry.getValue());
                items_nbt.appendTag(item);
            }
        }
        nbt.setTag("items",items_nbt);
        nbt.setInteger("max_size",maxsize);
        return nbt;
    }

    @Override
    public void saveNBT() {
        if(isStorage())
            stack.setTagCompound(serializeNBT());
    }

    @Override
    public HashMap<GeneralStack, Integer> getStorgeMap() {
        return items;
    }

    public int getMaxSize(){
        return isStorage()?maxsize:0;
    }

    public int getUsedSize(){
        if(!isStorage())
            return 0;
        int count=0;
        for(int i:items.values()){
            count+=i;
        }
        return count;
    }

    public int getUsedSize_logic(){
        if(!isStorage())
            return 0;
        int count=0;
        for(Map.Entry<GeneralStack,Integer> entry:items.entrySet()) {
            count+=entry.getKey().fluid?entry.getValue()/1000:entry.getValue();
        }
        return count;
    }

    @Override
    public boolean hasItem(GeneralStack itemlist) {
        if(!isStorage())
            return false;
        return items.containsKey(itemlist);
    }

    @Override
    public int getItemCount(GeneralStack itemlist) {
        if(!isStorage())
            return 0;
        return items.get(itemlist);
    }

    @Override
    public GeneralStack addItem(GeneralStack item_add, boolean doadd){
        if(!isStorage())
            return item_add;

        int count_add=item_add.getCount();
        int left_size=getMaxSize()-getUsedSize_logic();
        if(item_add.fluid){
            if (item_add.getCount()/1000f > left_size) {
                count_add = left_size*1000;
            }
        } else {
            if (item_add.getCount() > left_size) {
                count_add = left_size;
            }
        }

        if(doadd) {
            GeneralStack ish = item_add;
            if (items.containsKey(ish))
                items.put(ish, items.get(ish) + count_add);
            else
                items.put(ish, count_add);
        }

        if(count_add==item_add.getCount())
            return item_add.getEmpty();
        else {
            item_add.setCount(item_add.getCount()-count_add);
            return item_add;
        }
    }

    @Override
    public GeneralStack takeItem(GeneralStack item_take, int count, boolean dotake) {
        if(!isStorage())
            return item_take.getEmpty();
        GeneralStack ish = item_take;
        if(!items.containsKey(item_take))
            return item_take.getEmpty();
        int left=items.get(ish);
        GeneralStack res=item_take.copy();

        if(left>count) {
            res.setCount(count);
            if(dotake)
                items.put(ish,left-count);
        } else {
            res.setCount(left);
            if(dotake)
                items.remove(ish);
        }

        return res;
    }

}
