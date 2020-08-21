package com.qtransfer.mod7e.items;

import com.qtransfer.mod7e.energy.GeneralEnergy;
import com.qtransfer.mod7e.energy.QEnergyUser;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class QuantumSuperpositionBall implements INBTSerializable<NBTTagCompound> {

    public QEnergyUser energy=new QEnergyUser(1L<<50){
        @Override
        public long insertEnergy(long energy, boolean simulate) {
            long eng = super.insertEnergy(energy, simulate);
            writeNBT();
            return eng;
        }

        @Override
        public long extractEnergy(long energy, boolean simulate) {
            long eng = super.extractEnergy(energy, simulate);
            writeNBT();
            return eng;
        }
    };

    public EnergyStorage fe_energy=new EnergyStorage(Integer.MAX_VALUE){
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int eng_rst=(int)QuantumSuperpositionBall.this.energy.insertEnergy(maxReceive/4,simulate)*4;
            return maxReceive-eng_rst;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return (int)QuantumSuperpositionBall.this.energy.extractEnergy(maxExtract/4,simulate)*4;
        }

        @Override
        public int getEnergyStored() {
            long eng=QuantumSuperpositionBall.this.energy.getEnergy();
            return (int) (eng*4>=Integer.MAX_VALUE?Integer.MAX_VALUE:eng*4);
        }
    };

    boolean outfe=false;

    ItemStack stack;

    public QuantumSuperpositionBall(ItemStack stack){
        energy.setExtractSpeed(1L<<20);
        energy.setInsertSpeed(1L<<20);
        this.stack=stack;
        deserializeNBT(stack.getTagCompound());
    }

    public void writeNBT(){
        stack.setTagCompound(serializeNBT());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        nbt.setTag("energy",energy.serializeNBT());
        nbt.setBoolean("outfe",outfe);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt==null)
            return;
        energy.deserializeNBT(nbt.getCompoundTag("energy"));
        outfe=nbt.getBoolean("outfe");
    }
}
