package com.qtransfer.mod7e.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.Sys;

public class QRobotEntity extends EntityLiving {

    public Vec3d move_target;
    public double speed=0.9;

    public QRobotEntity(World worldIn) {
        super(worldIn);
        moveTo(getPositionVector().addVector(5,0,0));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        //检测是否移动到指定位置
        if(move_target!=null){
            getNavigator().tryMoveToXYZ(move_target.x,move_target.y,move_target.z,speed);
            System.out.println("move:"+getPositionVector());
            /*if(this.getPositionVector().distanceTo(move_target)<0.01){
                arriveTarget();
                move_target=null;
            }*/
        }
    }

    public void moveTo(Vec3d target){
        move_target=target;
    }

    public void arriveTarget(){
        System.out.println("arr at pos"+getPositionVector());
    }

}
