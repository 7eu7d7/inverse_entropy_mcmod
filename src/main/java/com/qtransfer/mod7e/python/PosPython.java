package com.qtransfer.mod7e.python;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PosPython {
    Vec3d pos;
    public PosPython(BlockPos pos){
        this(new Vec3d(pos));
    }
    public PosPython(Vec3d pos){
        this.pos=pos;
    }

    public PosPython(double x,double y,double z){
        pos=new Vec3d(x,y,z);
    }

    public double getX(){ return pos.x; }
    public double getY(){ return pos.y; }
    public double getZ(){ return pos.z; }

    public PosPython offset(EnumFacing facing){ return new PosPython(new BlockPos(pos).offset(facing)); }
    public PosPython offset(EnumFacing facing,int n){ return new PosPython(new BlockPos(pos).offset(facing,n)); }
    public PosPython add(double x,double y,double z){ return new PosPython(pos.addVector(x,y,z)); }
    public double getDistance(double x,double y,double z){ return pos.distanceTo(new Vec3d(x,y,z)); }
    public BlockPos toBlockPos(){
        return new BlockPos(pos);
    }

    //for python
    public PosPython __add__(PosPython pos){
        return new PosPython(this.pos.add(pos.pos));
    }
    public PosPython __sub__(PosPython pos){
        return new PosPython(this.pos.subtract(pos.pos));
    }
    public PosPython __mul__(double f){
        return new PosPython(pos.scale(f));
    }

    public PosPython __radd__(PosPython pos){
        return new PosPython(pos.pos.add(this.pos));
    }
    public PosPython __rsub__(PosPython pos){
        return new PosPython(pos.pos.subtract(this.pos));
    }
    public PosPython __rmul__(double f){
        return new PosPython(pos.scale(f));
    }

    @Override
    public String toString() {
        return pos.toString();
    }
}
