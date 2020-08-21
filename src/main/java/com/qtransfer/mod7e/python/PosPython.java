package com.qtransfer.mod7e.python;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class PosPython {
    BlockPos pos;
    public PosPython(BlockPos pos){
        this.pos=pos;
    }

    public PosPython(int x,int y,int z){
        pos=new BlockPos(x,y,z);
    }

    public int getX(){ return pos.getX(); }
    public int getY(){ return pos.getY(); }
    public int getZ(){ return pos.getZ(); }

    public PosPython offset(EnumFacing facing){ return new PosPython(pos.offset(facing)); }
    public PosPython offset(EnumFacing facing,int n){ return new PosPython(pos.offset(facing,n)); }
    public PosPython add(int x,int y,int z){ return new PosPython(pos.add(x,y,z)); }
    public double getDistance(int x,int y,int z){ return pos.getDistance(x,y,z); }

    @Override
    public String toString() {
        return pos.toString();
    }
}
