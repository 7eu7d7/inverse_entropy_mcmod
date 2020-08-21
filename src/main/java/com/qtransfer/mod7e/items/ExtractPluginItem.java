package com.qtransfer.mod7e.items;

import com.qtransfer.mod7e.utils.CraftItemHandler;
import com.qtransfer.mod7e.utils.FluidTankList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;

public class ExtractPluginItem implements INBTSerializable<NBTTagCompound> {

    ItemStack stack;
    public CraftItemHandler inventory_item=new CraftItemHandler(9);
    public FluidTankList inventory_fluid=new FluidTankList(9);
    public String target="";

    public ExtractPluginItem(ItemStack stack){
        this.stack=stack;
        deserializeNBT(stack.getTagCompound());
    }

    public void writeNBT(){
        stack.setTagCompound(serializeNBT());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        nbt.setTag("inventory_item",inventory_item.serializeNBT());
        nbt.setTag("inventory_fluid",inventory_fluid.serializeNBT());
        nbt.setString("target",target);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt==null)
            return;
        inventory_item.deserializeNBT(nbt.getCompoundTag("inventory_item"));
        inventory_fluid.deserializeNBT(nbt.getCompoundTag("inventory_fluid"));
        target=nbt.getString("target");
    }
}
