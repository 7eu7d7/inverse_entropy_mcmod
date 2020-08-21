package com.qtransfer.mod7e.transfer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GeneralStack implements INBTSerializable<NBTTagCompound> {
    public ItemStack stack;
    public FluidStack fstack;
    public boolean fluid=false;

    public GeneralStack(ItemStack stack){
        this.stack=stack;
    }
    public GeneralStack(FluidStack fstack){
        this.fstack=fstack;
        fluid=true;
    }

    public GeneralStack(NBTTagCompound nbt){
        deserializeNBT(nbt);
    }

    @Override
    public int hashCode() {
        return fluid?hashCode(fstack):hashCode(stack);
    }

    @Override
    public boolean equals(Object obj) {

        if(obj==this) {
            return true;
        }else if(!(obj instanceof GeneralStack)){
            return false;
        } else {
            if(fluid){
                return fstack.equals(((GeneralStack) obj).fstack);
            } else {
                ItemStack equ = ((GeneralStack) obj).stack;
                return equ!= null && ItemStack.areItemsEqualIgnoreDurability(stack, equ) &&
                        ItemStack.areItemStackTagsEqual(stack, equ) &&
                        stack.getMetadata() == equ.getMetadata();
            }
        }
    }

    @Override
    public String toString() {
        return fluid? (fstack==null?"null":("fluid:"+fstack.amount+"x "+fstack.getUnlocalizedName())):"item:"+stack.toString();
    }

    public String getName(){
        return fluid?fstack.getFluid().getName():stack.getDisplayName();
    }

    public GeneralStack copy(){
        return fluid?new GeneralStack(fstack.copy()):new GeneralStack(stack.copy());
    }
    public GeneralStack copy(int amount){
        if (fluid) {
            FluidStack tmp = fstack.copy();
            tmp.amount=amount;
            return new GeneralStack(tmp);
        } else {
            ItemStack tmp = stack.copy();
            tmp.setCount(amount);
            return new GeneralStack(tmp);
        }
    }

    public int getCount(){
        return fluid?fstack.amount:stack.getCount();
    }

    public void setCount(int count){
        if (fluid) {
            fstack.amount = count;
        } else {
            stack.setCount(count);
        }
    }

    public GeneralStack getEmpty(){
        return fluid?new GeneralStack(new FluidStack(FluidRegistry.WATER,0)):new GeneralStack(ItemStack.EMPTY);
    }

    public boolean isEmpty(){
        return fluid?(fstack==null || fstack.amount<=0) : (stack==null || stack.isEmpty());
    }

    public void grow(int n){
        if (fluid) {
            fstack.amount += n;
        } else {
            stack.grow(n);
        }
    }

    public static boolean stackEqual(ItemStack a,ItemStack b){
        return  a!=null && b!=null &&
                ItemStack.areItemsEqualIgnoreDurability(a,b) &&
                ItemStack.areItemStackTagsEqual(a,b) &&
                a.getMetadata()==b.getMetadata();
    }

    public static boolean stackEqual(FluidStack a,FluidStack b){
        return a.equals(b);
    }

    public static int hashCode(ItemStack stack) {
        if(stack.isEmpty())
            return 0;
        int hash=stack.getMetadata();
        if (stack.getTagCompound() != null) {
            hash=31*hash+stack.getTagCompound().hashCode();
        }
        hash=31*hash+stack.getItem().hashCode();
        return hash;
    }

    public static int hashCode(FluidStack stack) {
        return stack.getFluid().hashCode();
    }

    public static NBTTagCompound packNBT(ItemStack stack,int count){
        NBTTagCompound item_tag=stack.serializeNBT();
        item_tag.setInteger("real_count",count);
        return item_tag;
    }

    public static NBTTagCompound packNBT(GeneralStack stack){
        NBTTagCompound item_tag=stack.serializeNBT();
        item_tag.setInteger("real_count",stack.getCount());
        return item_tag;
    }

    public static GeneralStack unpackNBT(NBTTagCompound item_tag){
        GeneralStack stack=new GeneralStack(item_tag);
        stack.setCount(item_tag.getInteger("real_count"));
        return stack;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        nbt.setBoolean("is_fluid",fluid);

        if(fluid){
            nbt.setTag("fluid", fstack.writeToNBT(new NBTTagCompound()));
        } else {
            nbt.setTag("item", stack.serializeNBT());
        }
        nbt.setInteger("stack_count",getCount());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt.getBoolean("is_fluid")){
            fstack=FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluid"));
            fluid=true;
        } else {
            stack=new ItemStack(nbt.getCompoundTag("item"));
            fluid=false;
        }
        setCount(nbt.getInteger("stack_count"));
    }
}
