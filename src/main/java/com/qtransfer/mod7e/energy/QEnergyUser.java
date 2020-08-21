package com.qtransfer.mod7e.energy;

public class QEnergyUser extends QEnergyStorage implements IQEUser{
    public QEnergyUser(long cap) {
        super(cap);
    }
    public QEnergyUser(long cap,long ist_speed) {
        super(cap,cap,ist_speed);
    }

    @Override
    public boolean useEnergy(long count) {
        if(count>energy)
            return false;
        else {
            energy-=count;
            return true;
        }
    }
}
