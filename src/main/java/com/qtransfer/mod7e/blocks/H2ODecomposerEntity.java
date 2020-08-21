package com.qtransfer.mod7e.blocks;

import com.qtransfer.mod7e.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public final class H2ODecomposerEntity extends QTransTileEntity implements IFluidHandler,ITickable{

    public int progress;

    public final ItemStackHandler inventory_itemin = new ItemStackHandler(1);
    public final ItemStackHandler inventory_in = new ItemStackHandler(3);
    public final ItemStackHandler inventory_out = new ItemStackHandler(3);
    public FluidTank tank_o2 = new FluidTank(64000);
    public FluidTank tank = new FluidTank(64000);
    public FluidTank tank_h2 = new FluidTank(64000);

    //private ItemStack result;
    boolean can_prog;
    public final int per_ticks=4;

    @Override
    public void update() {
        if (!world.isRemote) {
            //System.out.println("mmp"+tank.getFluidAmount());
            // IItemHandler 的下标从 0 开始。

            if(progress==0){
                if(!inventory_itemin.getStackInSlot(0).isEmpty() && inventory_itemin.getStackInSlot(0).getItem() == Item.getByNameOrId("minecraft:glowstone_dust") &&
                        tank.getFluidAmount()>=1000 && can_output()){
                    tank.drainInternal(1000, true);
                    inventory_itemin.extractItem(0,1,false);
                    can_prog=true;
                    //result=FurnaceRecipes.instance().getSmeltingResult(inventory.extractItem(0,1,false)).copy();
                    syncToTrackingClients();
                }else{
                    can_prog=false;
                }
            }

            if(can_prog) {
                ++progress;
                if (progress > per_ticks) {
                    progress = 0;
                    tank_o2.fill(FluidRegistry.getFluidStack("oxygen",1000),true);
                    tank_h2.fill(FluidRegistry.getFluidStack("hydrogen",2000),true);

                    //tank_o2.fill(FluidRegistry.getFluidStack("advancedrocketry:oxygenfluid",1000),true);
                    //tank_h2.fill(FluidRegistry.getFluidStack("advancedrocketry:hydrogenfluid",2000),true);
                    syncToTrackingClients();
                    markDirty();
                    //dropItemStackAsEntity(this.world, getPos(),FurnaceRecipes.instance().getSmeltingResult(inventory.extractItem(0,1,false)));
                }
            }
            IFluidHandler handler;
            if(!inventory_in.getStackInSlot(0).isEmpty() && hasFluid(inventory_in.getStackInSlot(0)) &&
                    FluidUtil.getFluidContained(inventory_in.getStackInSlot(0)).getFluid()==FluidRegistry.WATER){

                FluidActionResult fas=FluidUtil.tryEmptyContainer(inventory_in.getStackInSlot(0), tank, Integer.MAX_VALUE,null, true);
                if(fas.isSuccess()){
                    inventory_in.extractItem(0,1,false);
                    inventory_out.insertItem(0,fas.getResult(),false);
                    syncToTrackingClients();
                }
            }
            if(!inventory_in.getStackInSlot(1).isEmpty() && (!hasFluid(inventory_in.getStackInSlot(1)) ||
                    FluidUtil.getFluidContained(inventory_in.getStackInSlot(1)).getFluid()==FluidRegistry.getFluid("oxygen")) ){
                FluidActionResult fas=FluidUtil.tryFillContainer(inventory_in.getStackInSlot(1), tank_o2, Integer.MAX_VALUE,null, true);
                if(fas.isSuccess()){
                    inventory_in.extractItem(1,1,false);
                    inventory_out.insertItem(1,fas.getResult(),false);
                    syncToTrackingClients();
                }
            }
            if(!inventory_in.getStackInSlot(2).isEmpty() && (!hasFluid(inventory_in.getStackInSlot(2)) ||
                    FluidUtil.getFluidContained(inventory_in.getStackInSlot(2)).getFluid()==FluidRegistry.getFluid("hydrogen"))){
                FluidActionResult fas=FluidUtil.tryFillContainer(inventory_in.getStackInSlot(2), tank_h2, Integer.MAX_VALUE,null, true);
                if(fas.isSuccess()){
                    inventory_in.extractItem(2,1,false);
                    inventory_out.insertItem(2,fas.getResult(),false);
                    syncToTrackingClients();
                }
            }
        }
    }

    private boolean can_output(){
        return tank_o2.getFluidAmount()<=tank_o2.getCapacity()-1000 && tank_h2.getFluidAmount()<=tank_h2.getCapacity()-2000;
    }

    private boolean hasFluid(ItemStack is){
        return FluidUtil.getFluidContained(is)!=null;
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
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory_itemin);
        } else {
            return super.getCapability(cap, facing);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        tank.readFromNBT(tag.getCompoundTag("tank_in"));
        tank_h2.readFromNBT(tag.getCompoundTag("tank_h2"));
        tank_o2.readFromNBT(tag.getCompoundTag("tank_o2"));
        this.progress = tag.getInteger("Progress");
        this.inventory_in.deserializeNBT(tag.getCompoundTag("Inventory_in"));
        this.inventory_out.deserializeNBT(tag.getCompoundTag("Inventory_out"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("tank_in",tank.writeToNBT(new NBTTagCompound()));
        tag.setTag("tank_o2",tank_o2.writeToNBT(new NBTTagCompound()));
        tag.setTag("tank_h2",tank_h2.writeToNBT(new NBTTagCompound()));
        tag.setInteger("Progress", this.progress);
        tag.setTag("Inventory_in", this.inventory_in.serializeNBT());
        tag.setTag("Inventory_out", this.inventory_out.serializeNBT());
        super.writeToNBT(tag);
        return tag;
    }

    //IFluidHandler
    @Override
    public IFluidTankProperties[] getTankProperties() {
        return Utils.catArray(tank.getTankProperties(),tank_o2.getTankProperties(),tank_h2.getTankProperties());
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if(resource.getFluid()== FluidRegistry.WATER) {
            int res = tank.fill(resource, doFill);
            syncToTrackingClients();
            return res;
        }
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        FluidStack res=null;
        if(resource.getFluid()==FluidRegistry.getFluid("oxygen"))
            res=tank_o2.drain(resource,doDrain);
        else if(resource.getFluid()==FluidRegistry.getFluid("hydrogen"))
            res=tank_h2.drain(resource,doDrain);
        syncToTrackingClients();
        return res;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack res = tank_h2.getFluidAmount()>0?tank_h2.drain(maxDrain,doDrain):tank_o2.drain(maxDrain,doDrain);
        syncToTrackingClients();
        return res;
    }
}
