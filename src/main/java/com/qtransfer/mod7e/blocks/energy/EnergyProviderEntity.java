package com.qtransfer.mod7e.blocks.energy;

import com.qtransfer.mod7e.blocks.QTransBlock;
import com.qtransfer.mod7e.blocks.QTransTileEntity;
import com.qtransfer.mod7e.energy.CapabilityQEnergy;
import com.qtransfer.mod7e.energy.IQEProvider;
import com.qtransfer.mod7e.energy.IQEUser;
import com.qtransfer.mod7e.energy.QEnergyProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

public class EnergyProviderEntity extends QTransTileEntity implements ITickable{
    public QEnergyProvider energyProvider=new QEnergyProvider(10000000);
    public boolean initok=false;

    public EnergyProviderEntity(){
        super();
    }

    @Override
    public void update() {
        //输出能量
        //System.out.println(this);
        if(world.isRemote)
            return;

        if (!initok){
            initok=true;
            QTransBlock.updateEnergyNet(getWorld(),getPos());
        }
        getCapability(CapabilityQEnergy.QENERGY_PROVIDER,null).sendEnergy();
        /*long rest_energy=provider.extractEnergy(provider.getEnergy(),false);
        if(provider.users!=null)
        for(IQEUser user:provider.users){
            if(rest_energy<=0)
                break;
            rest_energy=user.insertEnergy(rest_energy,false);
        }
        provider.insertEnergy(rest_energy,false);*/
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energyProvider.deserializeNBT(tag.getCompoundTag("energy_provider"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("energy_provider", this.energyProvider.serializeNBT());
        return super.writeToNBT(tag);
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
        return cap == CapabilityQEnergy.QENERGY_PROVIDER || super.hasCapability(cap, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
        if (cap == CapabilityQEnergy.QENERGY_PROVIDER) {
            return CapabilityQEnergy.QENERGY_PROVIDER.cast(energyProvider);
        } else {
            return super.getCapability(cap, facing);
        }
    }
}
