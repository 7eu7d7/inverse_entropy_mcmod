package com.qtransfer.mod7e.blocks.energy;

import com.qtransfer.mod7e.energy.CapabilityQEnergy;
import com.qtransfer.mod7e.energy.GeneralEnergy;
import com.qtransfer.mod7e.energy.QEnergyProvider;
import com.qtransfer.mod7e.energy.QEnergyUser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class EnergyConverterEntity extends EnergyProviderEntity {
    public GeneralEnergy gene_energy=new GeneralEnergy();
    public HashMap<BlockPos,IEnergyStorage> fe_map=new HashMap<BlockPos, IEnergyStorage>();
    boolean feinit=false;

    public void initfe(){
        for(EnumFacing face:EnumFacing.VALUES){
            BlockPos fromPos=pos.offset(face);
            if(world.getTileEntity(fromPos)!=null &&
                    world.getTileEntity(fromPos).hasCapability(CapabilityEnergy.ENERGY, face.getOpposite())){
                fe_map.put(fromPos,world.getTileEntity(fromPos).getCapability(CapabilityEnergy.ENERGY, face.getOpposite()));
            }
        }
    }

    @Override
    public void update() {
        super.update();
        if(!feinit) initfe();

        //向外输出FE能量
        if(fe_map.size()>0) {
            EnergyStorage es = gene_energy.getFEEnergy();
            int eng = es.extractEnergy(es.getEnergyStored(), false);
            for (IEnergyStorage ies : fe_map.values()) {
                if (eng <= 0)
                    break;
                eng -= ies.receiveEnergy(eng, false);
            }
            es.receiveEnergy(eng, false);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
        return cap == CapabilityQEnergy.QENERGY_USER || cap == CapabilityEnergy.ENERGY || super.hasCapability(cap, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
        if (cap == CapabilityQEnergy.QENERGY_USER) {
            return CapabilityQEnergy.QENERGY_USER.cast(gene_energy.getUser());
        } else if (cap == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(gene_energy.getFEEnergy());
        } else if (cap == CapabilityQEnergy.QENERGY_PROVIDER) {
            return CapabilityQEnergy.QENERGY_PROVIDER.cast(gene_energy.getProvider());
        } else {
            return super.getCapability(cap, facing);
        }
    }
}
