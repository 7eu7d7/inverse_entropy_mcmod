package com.qtransfer.mod7e.utils;

import com.qtransfer.mod7e.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.Vector;

public class FluidTankList implements IFluidHandler, INBTSerializable<NBTTagCompound> {

    public Vector<FluidTank> flu_list=new Vector<FluidTank>();

    public FluidTankList(int size){
        for(int i=0;i<size;i++){
            flu_list.add(new FluidTank(100*1000));
        }
    }

    public FluidTankList(NBTTagCompound nbt){
        deserializeNBT(nbt);
    }

    public int size(){
        return flu_list.size();
    }

    public FluidTank get(int idx){
        return flu_list.get(idx);
    }

    public FluidTank addTank(){
        flu_list.add(new FluidTank(100*1000));
        return flu_list.lastElement();
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        IFluidTankProperties[][] pros=new  IFluidTankProperties[flu_list.size()][];
        for(int i=0;i<flu_list.size();i++)
            pros[i]=flu_list.get(i).getTankProperties();
        return Utils.catArray(pros);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        resource=resource.copy();
        int total=0;
        for(FluidTank ft:flu_list){
            if(ft.canFillFluidType(resource)) {
                int tmp = ft.fill(resource, doFill);
                resource.amount-=tmp;
                total+=tmp;
                if(resource.amount<=0)
                    break;
            }
        }
        return total;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        resource=resource.copy();
        FluidStack res=resource.copy();
        res.amount=0;
        for(FluidTank ft:flu_list){
            if(ft.canDrainFluidType(resource)) {
                int tmp = ft.drain(resource, doDrain).amount;
                resource.amount-=tmp;
                res.amount+=tmp;
                if(resource.amount<=0)
                    break;
            }
        }
        return res;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack res=null;
        for(FluidTank ft:flu_list){
            if(res==null){
                res=ft.drain(maxDrain,doDrain);
                maxDrain-=res.amount;
            }else if(ft.canDrainFluidType(res)) {
                int tmp = ft.drain(maxDrain, doDrain).amount;
                maxDrain-=tmp;
                res.amount+=tmp;
                if(maxDrain<=0)
                    break;
            }
        }
        return res;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        NBTTagList list=new NBTTagList();
        for(FluidTank ft :flu_list)
            list.appendTag(ft.writeToNBT(new NBTTagCompound()));
        nbt.setTag("tanks",list);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(flu_list.size()<=0)
            return;
        //flu_list.clear();
        NBTTagList list=nbt.getTagList("tanks",10);
        for(int i=0;i<list.tagCount();i++){
            if(i>=flu_list.size())
                flu_list.add(new FluidTank(100*1000));
            flu_list.get(i).readFromNBT(list.getCompoundTagAt(i));
        }
    }
}
