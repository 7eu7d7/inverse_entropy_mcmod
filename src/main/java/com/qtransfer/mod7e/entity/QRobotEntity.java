package com.qtransfer.mod7e.entity;

import com.google.common.collect.EvictingQueue;
import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.blocks.QTransTileEntity;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import com.qtransfer.mod7e.items.SingleChipItem;
import com.qtransfer.mod7e.python.QRobotPython;
import com.qtransfer.mod7e.utils.FixedList;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.Sys;

import javax.annotation.Nullable;
import java.util.Queue;

public class QRobotEntity extends EntityLiving {

    public ItemStackHandler inventory_chip = new ItemStackHandler(1);
    public ItemStackHandler inventory_storage = new ItemStackHandler(27);

    public SingleChipItem chip;
    volatile QRobotPython robot_script;

    public volatile Vec3d move_target;
    public double speed=0.5;

    FixedList<Vec3d> q_move = new FixedList<Vec3d>(10); //判断是否卡住
    int stack_count=0;

    public static final int det_range=32;

    public QRobotEntity(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if(!world.isRemote) {

            //检测是否移动到指定位置
            if (move_target != null) {
                if(getNavigator().noPath()) {
                    getMoveHelper().setMoveTo(move_target.x, move_target.y, move_target.z, speed);
                    //System.out.println("target:"+move_target);
                }
                //System.out.println("move:"+getPositionVector());
                q_move.offer(getPositionVector());

                if(q_move.size()>5){
                    if(q_move.getLast().distanceTo(q_move.get(q_move.size()-2))>0.3){ //还在移动
                        q_move.clear();
                        stack_count=0;
                    }
                }

                if(q_move.full()){
                    if(q_move.getLast().distanceTo(q_move.getFirst())<0.4){ //被卡住了
                        jump();
                        stack_count++;
                    }
                }

                if(stack_count>10){ //目标不可达
                    stopChip();
                    move_target=null;
                    return;
                }

                if (this.getPositionVector().distanceTo(move_target) < 0.15) {
                    setPosition(move_target.x, move_target.y, move_target.z);
                    robot_script.arriveTarget();
                    move_target = null;
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setTag("inventory_chip", this.inventory_chip.serializeNBT());
        compound.setTag("inventory_storage", this.inventory_storage.serializeNBT());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.inventory_chip.deserializeNBT(compound.getCompoundTag("inventory_chip"));
        this.inventory_storage.deserializeNBT(compound.getCompoundTag("inventory_storage"));
        super.readEntityFromNBT(compound);
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            player.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_Q_ROBOT, world, getEntityId(),0,0);
            return true;
        }
        return super.processInteract(player, hand);
    }

    public boolean moveTo(Vec3d target){
        if(!world.isAirBlock(new BlockPos(target)))
            return false;
        move_target=target;
        return true;
    }

    public void startChip(){
        if(inventory_chip.getStackInSlot(0).isEmpty()) {
            chip = null;
            return;
        }

        chip=new SingleChipItem(inventory_chip.getStackInSlot(0));
        chip.initScript((interpreter)->{
            interpreter.set("entity", robot_script=new QRobotPython(this));
        });
        //chip.callFunction_nowait("main",);
        System.out.println("start python");
    }

    public void stopChip(){
        if(inventory_chip.getStackInSlot(0).isEmpty()) {
            chip = null;
            return;
        }
        if(chip!=null && chip.th_run!=null)
            chip.stopRunning();
        System.out.println("stop python");
    }

    /*@Override
    protected PathNavigate createNavigator(World worldIn) {
        return new PathNavigateFlying(this, worldIn);
    }*/

    /*@Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        System.out.println("apply attr:"+det_range);
        this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0d);
    }*/

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }
}
