package com.qtransfer.mod7e.energy;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class QEnergyGroup implements IQEUser,IQEProvider{
    public ArrayList<IQEnergy> group=new ArrayList<IQEnergy>();
    public List<IQEUser> users;
    public long ext_speed=1<<20;
    public long ist_speed=1<<20;

    public void addQEnergy(IQEnergy energy){
        group.add(energy);
    }

    public void removeAll(){
        group.clear();
    }

    @Override
    public long getEnergy() {
        long total=0;
        for (IQEnergy energy:group)
            total+=energy.getEnergy();
        return total;
    }

    @Override
    public long getCapacity() {
        long total=0;
        for (IQEnergy energy:group)
            total+=energy.getCapacity();
        return total;
    }

    @Override
    public void setEnergy(long energy) {

    }

    @Override
    public void setCapacity(long cap) {

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
        long eng_ist=Math.min(energy,ist_speed);
        long rest=energy-eng_ist;
        for (IQEnergy qenergy:group) {
            eng_ist = qenergy.insertEnergy(eng_ist, simulate);
            if(eng_ist<=0)
                break;
        }
        return rest+eng_ist;
    }

    @Override
    public long extractEnergy(long energy, boolean simulate) {
        long eng_ext=Math.min(energy,ext_speed);
        long ext=0;
        for (IQEnergy qenergy:group) {
            long exted = qenergy.extractEnergy(eng_ext, simulate);
            eng_ext-=exted;
            ext+=exted;
            if(eng_ext<=0)
                break;
        }
        return ext;
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
    public void setUsers(List<IQEUser> users) {
        this.users=users;
    }

    @Override
    public void sendEnergy() {
        if(users!=null)
            for(IQEUser user:users){
                if(getEnergy()>0) {
                    insertEnergy(user.insertEnergy(extractEnergy(ext_speed,false),false),false);
                }
            }
    }

    @Override
    public boolean useEnergy(long count) {
        if(count>getEnergy())
            return false;
        else {
            extractEnergy(count,false);
            return true;
        }
    }
}
