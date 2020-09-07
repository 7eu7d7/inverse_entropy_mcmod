package com.qtransfer.mod7e.python;

import net.minecraft.entity.EntityLiving;

public class EntityPython extends CommonPython{
    protected EntityLiving entity;
    protected int pos_mode=2;

    public static final int ABS_MODE=1;
    public static final int REL_MODE=2;

    public EntityPython(EntityLiving entity){
        this.entity=entity;
    }

    public String getBlockName(PosPython pos){
        return entity.world.getBlockState(processPos(pos).toBlockPos()).getBlock().getRegistryName().toString();
    }

    public PosPython getPos(){
        return new PosPython(entity.getPositionVector());
    }

    public PosPython processPos(PosPython pos){
        if(pos_mode==ABS_MODE)
            return pos;
        else if(pos_mode==REL_MODE)
            return pos.add((int)entity.posX,(int)entity.posY,(int)entity.posZ);
        return pos;
    }

    public void setPosMode(int pos_mode) {
        this.pos_mode = pos_mode;
    }
}
