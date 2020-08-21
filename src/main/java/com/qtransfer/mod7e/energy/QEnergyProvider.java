package com.qtransfer.mod7e.energy;

import java.util.List;

public class QEnergyProvider extends QEnergyStorage implements IQEProvider{
    public List<IQEUser> users;


    public QEnergyProvider(long cap) {
        super(cap);
    }

    public QEnergyProvider(long cap,long ext_speed) {
        super(cap,ext_speed,cap);
    }

    @Override
    public void setUsers(List<IQEUser> users) {
        this.users=users;
    }

    @Override
    public void sendEnergy() {
        //System.out.println("send");
        if(users!=null)
        for(IQEUser user:users){
            if(energy>0) {
                //System.out.println("per "+energy);
                //System.out.println(energy+","+user.getEnergy());
                //setEnergy(user.insertEnergy(getEnergy(),false));
                insertEnergy(user.insertEnergy(extractEnergy(ext_speed,false),false),false);
                //System.out.println("after "+energy);
            }
        }
    }
}
