package com.qtransfer.mod7e.entity;

import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.blocks.QTransTileEntity;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import com.qtransfer.mod7e.items.SingleChipItem;
import com.qtransfer.mod7e.python.QRobotPython;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

public class QRobotEntity extends EntityLiving {

    public ItemStackHandler inventory_chip = new ItemStackHandler(1);
    public ItemStackHandler inventory_storage = new ItemStackHandler(27);

    public SingleChipItem chip;
    QRobotPython robot_script;

    public Vec3d move_target;
    public double speed=0.5;

    public QRobotEntity(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if(!world.isRemote) {

            //检测是否移动到指定位置
            if (move_target != null) {
                getMoveHelper().setMoveTo(move_target.x,move_target.y,move_target.z,speed);
                System.out.println("move:"+getPositionVector());
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

    public void moveTo(Vec3d target){
        move_target=target;
    }

    public void startChip(){
        if(inventory_chip.getStackInSlot(0).isEmpty()) {
            chip = null;
            return;
        }

        chip=new SingleChipItem(inventory_chip.getStackInSlot(0));
        chip.initScript();
        chip.runFuncThread("main",robot_script=new QRobotPython(this));
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

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }
}
