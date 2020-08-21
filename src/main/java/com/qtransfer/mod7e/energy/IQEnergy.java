package com.qtransfer.mod7e.energy;

public interface IQEnergy {

    long getEnergy();
    long getCapacity();

    void setEnergy(long energy);
    void setCapacity(long cap);
    void setInsertSpeed(long speed);
    void setExtractSpeed(long speed);

    long insertEnergy(long energy, boolean simulate); //返回未输入的能量
    long extractEnergy(long energy,boolean simulate);

    long getInsertSpeed();
    long getExtractSpeed();

}
