package com.qtransfer.mod7e.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class QEnergyStorage implements IQEnergy,INBTSerializable<NBTTagCompound>{
    public long energy;
    public long cap;
    public long ext_speed;
    public long ist_speed;

    public QEnergyStorage(long cap){
        this(cap,cap,cap);
    }
    public QEnergyStorage(long cap,long ext_speed,long ist_speed){
        setCapacity(cap);
        this.ext_speed=ext_speed;
        this.ist_speed=ist_speed;
    }


    @Override
    public long getEnergy() {
        return energy;
    }

    @Override
    public long getCapacity() {
        return cap;
    }

    @Override
    public void setEnergy(long energy) {
        this.energy=energy;
    }

    @Override
    public void setCapacity(long cap) {
        this.cap=cap;
    }

    @Override
    public void setInsertSpeed(long speed) {
        ist_speed=speed;
    }

    @Override
    public void setExtractSpeed(long speed) {
        ext_speed=speed;
    }

    @Override
    public long insertEnergy(long energy, boolean simulate) {
        long en=Math.min(Math.min(energy,cap-this.energy),getInsertSpeed());
        if(!simulate)
            this.energy+=en;
        return energy-en;
    }

    @Override
    public long extractEnergy(long energy, boolean simulate) {
        long en=Math.min(Math.min(energy,this.energy),getExtractSpeed());
        if(!simulate)
            this.energy-=en;
        return en;
    }

    @Override
    public long getInsertSpeed() {
        return ist_speed;
    }

    @Override
    public long getExtractSpeed() {
        return ext_speed;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        nbt.setLong("energy",getEnergy());
        nbt.setLong("cap",getCapacity());
        nbt.setLong("ext_speed",ext_speed);
        nbt.setLong("ist_speed",ist_speed);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        setEnergy(nbt.getLong("energy"));
        setCapacity(nbt.getLong("cap"));
        ext_speed=nbt.getLong("ext_speed");
        ist_speed=nbt.getLong("ist_speed");
    }
}
