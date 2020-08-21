package com.qtransfer.mod7e.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fluids.FluidTank;
import org.python.antlr.ast.Str;

import java.util.ArrayList;
import java.util.Collections;

public class AdvanceCraftPlugin extends CraftPluginItem{
    public ArrayList<String> item_send=new ArrayList<String>();
    public ArrayList<String> fluid_send=new ArrayList<String>();

    public AdvanceCraftPlugin(ItemStack stack) {
        super(stack,false);
        deserializeNBT(stack.getTagCompound());
        if(item_send.size()==0)item_send.add("");
        if(fluid_send.size()==0)fluid_send.add("");
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setTag("item_send", strs2nbt(item_send));
        nbt.setTag("fluid_send", strs2nbt(fluid_send));
        /*nbt.setString("item_send",String.join(",", item_send));
        nbt.setString("fluid_send",String.join(",", fluid_send));*/
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt==null)
            return;
        super.deserializeNBT(nbt);
        item_send.clear();
        NBTTagList item_list=nbt.getTagList("item_send", 8);
        item_list.forEach(x -> item_send.add(((NBTTagString)x).getString()));

        fluid_send.clear();
        NBTTagList fluid_list=nbt.getTagList("fluid_send", 8);
        fluid_list.forEach(x -> fluid_send.add(((NBTTagString)x).getString()));
    }

    public NBTTagList strs2nbt(ArrayList<String> strs){
        NBTTagList nbtlist=new NBTTagList();
        strs.forEach(x -> nbtlist.appendTag(new NBTTagString(x)));
        return nbtlist;
    }

    @Override
    public int addItemStuff() {
        item_send.add("");
        return super.addItemStuff();
    }

    @Override
    public FluidTank addFluidStuff() {
        fluid_send.add("");
        return super.addFluidStuff();
    }
}
