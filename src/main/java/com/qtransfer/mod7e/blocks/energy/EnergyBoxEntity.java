package com.qtransfer.mod7e.blocks.energy;

import com.qtransfer.mod7e.blocks.QTransBlock;
import com.qtransfer.mod7e.energy.CapabilityQEnergy;
import com.qtransfer.mod7e.energy.IQEUser;
import com.qtransfer.mod7e.energy.QEnergyGroup;
import com.qtransfer.mod7e.items.StorageItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;

public class EnergyBoxEntity extends EnergyProviderEntity{
    public ItemStackHandler inventory_energy = new ItemStackHandler(9);
    QEnergyGroup qe_cap=new QEnergyGroup();
    boolean init_ok=false;
    //ArrayList<IQEUser> qe_cap=new ArrayList<IQEUser>();


    @Override
    public void update() {
        if(world.isRemote)
            return;

        if (!initok){
            initok=true;
            QTransBlock.updateEnergyNet(getWorld(),getPos());
        }
        getCapability(CapabilityQEnergy.QENERGY_PROVIDER,EnumFacing.NORTH).sendEnergy();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory_energy.deserializeNBT(compound.getCompoundTag("inventory"));
        //if(!world.isRemote)
        if(!init_ok) {
            updateEnergy();
            init_ok=true;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory", inventory_energy.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
        if(cap == CapabilityQEnergy.QENERGY_PROVIDER && facing==EnumFacing.NORTH)
            return true;
        if(cap == CapabilityQEnergy.QENERGY_USER && facing!=EnumFacing.NORTH)
            return true;
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
        if (cap == CapabilityQEnergy.QENERGY_PROVIDER && facing==EnumFacing.NORTH) {
            return CapabilityQEnergy.QENERGY_PROVIDER.cast(qe_cap);
        } else if (cap == CapabilityQEnergy.QENERGY_USER && facing!=EnumFacing.NORTH) {
            return CapabilityQEnergy.QENERGY_USER.cast(qe_cap);
        } else {
            return null;
            //return super.getCapability(cap, facing);
        }
    }

    public void updateEnergy(){
        qe_cap.removeAll();
        for(int i=0;i<inventory_energy.getSlots();i++) {
            ItemStack stack=inventory_energy.getStackInSlot(i);
            if(stack.hasCapability(CapabilityQEnergy.QENERGY_USER,null))
                qe_cap.addQEnergy(stack.getCapability(CapabilityQEnergy.QENERGY_USER,null));
        }
    }
}
