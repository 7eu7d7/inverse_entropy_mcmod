package com.qtransfer.mod7e.python;

import com.qtransfer.mod7e.blocks.transfer.QuantumInterfaceEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class QInterfacePython extends EntityPython{
    QuantumInterfaceEntity interfaceEntity;

    public QInterfacePython(QuantumInterfaceEntity entity) {
        super(entity);
        interfaceEntity=entity;
    }

    public Object sendToInterface(String name,Object obj){
        return interfaceEntity.stabilizer.qi_list.get(name).onObjectArrive(obj);
    }

    public boolean hasItnerface(String name){
        return interfaceEntity.stabilizer.qi_list.containsKey(name);
    }

    public List<String> getInterfaceList(){
        return new ArrayList<>(interfaceEntity.stabilizer.qi_list.keySet());
    }
}
