package com.qtransfer.mod7e.energy;

import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class GeneralEnergy{
    long total_energy;
    long cap=100000;

    QEnergyUser user=new QEnergyUser(cap){
        @Override
        public long insertEnergy(long energy, boolean simulate) {
            super.setEnergy(total_energy);
            long eng = super.insertEnergy(energy, simulate);
            total_energy=super.getEnergy();
            super.setEnergy(0);
            return eng;
        }

        @Override
        public long extractEnergy(long energy, boolean simulate) {
            super.setEnergy(total_energy);
            long eng = super.extractEnergy(energy, simulate);
            total_energy=super.getEnergy();
            super.setEnergy(0);
            return eng;
        }

        @Override
        public long getEnergy() {
            return total_energy;
        }

        @Override
        public void setEnergy(long energy) {
            total_energy=energy;
        }
    };

    QEnergyProvider prov=new QEnergyProvider(cap){
        @Override
        public long insertEnergy(long energy, boolean simulate) {
            super.setEnergy(total_energy);
            long eng = super.insertEnergy(energy, simulate);
            total_energy=super.getEnergy();
            super.setEnergy(0);
            return eng;
        }

        @Override
        public long extractEnergy(long energy, boolean simulate) {
            super.setEnergy(total_energy);
            long eng = super.extractEnergy(energy, simulate);
            total_energy=super.getEnergy();
            super.setEnergy(0);
            return eng;
        }

        @Override
        public long getEnergy() {
            return total_energy;
        }

        @Override
        public void setEnergy(long energy) {
            total_energy=energy;
        }
    };

    EnergyStorage fe_energy=new EnergyStorage((int) (cap*4)){
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            energy= (int) (total_energy*4);
            int eng = super.receiveEnergy(maxReceive, simulate);
            total_energy=energy/4;
            energy=0;
            return eng;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            energy= (int) (total_energy*4);
            int eng = super.extractEnergy(maxExtract, simulate);
            total_energy=energy/4;
            energy=0;
            return eng;
        }

        @Override
        public int getEnergyStored() {
            return (int) (total_energy*4);
        }
    };

    public QEnergyUser getUser(){
        return user;
    }

    public QEnergyProvider getProvider() {
        return prov;
    }

    public EnergyStorage getFEEnergy() {
        return fe_energy;
    }
}
