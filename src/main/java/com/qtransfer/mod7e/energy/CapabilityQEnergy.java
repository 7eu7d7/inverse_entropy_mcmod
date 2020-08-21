package com.qtransfer.mod7e.energy;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityQEnergy implements ICapabilitySerializable<NBTTagCompound> {
    @CapabilityInject(IQEProvider.class)
    public static Capability<IQEProvider> QENERGY_PROVIDER=null;

    @CapabilityInject(IQEUser.class)
    public static Capability<IQEUser> QENERGY_USER=null;

    private IQEUser instance_user;
    private IQEProvider instance_prov;


    public CapabilityQEnergy(){
        instance_user = QENERGY_USER.getDefaultInstance();
        instance_prov = QENERGY_PROVIDER.getDefaultInstance();
    }

    public static void register()
    {
        //System.exit(0);
        CapabilityManager.INSTANCE.register(IQEProvider.class, new Capability.IStorage<IQEProvider>() {
            @Override
            public NBTBase writeNBT(Capability<IQEProvider> cap, IQEProvider instance, EnumFacing side) {
                NBTTagCompound nbt=new NBTTagCompound();
                nbt.setLong("energy",instance.getEnergy());
                nbt.setLong("cap",instance.getCapacity());
                nbt.setLong("ext_speed",instance.getExtractSpeed());
                nbt.setLong("ist_speed",instance.getInsertSpeed());
                return nbt;
            }
            @Override
            public void readNBT(Capability<IQEProvider> cap, IQEProvider instance, EnumFacing side, NBTBase data) {
                NBTTagCompound nbt=(NBTTagCompound) data;
                instance.setEnergy(nbt.getLong("energy"));
                instance.setCapacity(nbt.getLong("cap"));
                instance.setExtractSpeed(nbt.getLong("ext_speed"));
                instance.setInsertSpeed(nbt.getLong("ist_speed"));
            }
        }, () -> new QEnergyProvider(1000) );

        CapabilityManager.INSTANCE.register(IQEUser.class, new Capability.IStorage<IQEUser>() {
            @Override
            public NBTBase writeNBT(Capability<IQEUser> cap, IQEUser instance, EnumFacing side) {
                NBTTagCompound nbt=new NBTTagCompound();
                nbt.setLong("energy",instance.getEnergy());
                nbt.setLong("cap",instance.getCapacity());
                nbt.setLong("ext_speed",instance.getExtractSpeed());
                nbt.setLong("ist_speed",instance.getInsertSpeed());
                return nbt;
            }
            @Override
            public void readNBT(Capability<IQEUser> cap, IQEUser instance, EnumFacing side, NBTBase data) {
                NBTTagCompound nbt=(NBTTagCompound) data;
                instance.setEnergy(nbt.getLong("energy"));
                instance.setCapacity(nbt.getLong("cap"));
                instance.setExtractSpeed(nbt.getLong("ext_speed"));
                instance.setInsertSpeed(nbt.getLong("ist_speed"));
            }
        }, () -> new QEnergyUser(1000) );

    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability==QENERGY_USER || capability==QENERGY_PROVIDER;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability==QENERGY_PROVIDER){
            return QENERGY_PROVIDER.<T> cast(this.instance_prov);
        }else if(capability==QENERGY_USER){
            return QENERGY_USER.<T> cast(this.instance_user);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        nbt.setTag("prov", QENERGY_PROVIDER.getStorage().writeNBT(QENERGY_PROVIDER,instance_prov,null));
        nbt.setTag("user", QENERGY_USER.getStorage().writeNBT(QENERGY_USER,instance_user,null));
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        QENERGY_PROVIDER.getStorage().readNBT(QENERGY_PROVIDER,instance_prov,null,nbt.getTag("prov"));
        QENERGY_USER.getStorage().readNBT(QENERGY_USER,instance_user,null,nbt.getTag("user"));
    }
}
