package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.blocks.BlockShaperEntity;
import com.qtransfer.mod7e.entity.QRobotEntity;
import com.qtransfer.mod7e.proxy.BasePacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerQRobot extends ContainerBase implements ISyncable{
    QRobotEntity entity;

    public ContainerQRobot(EntityPlayer player, Entity entity){
        super();
        int offy=20,offx=40;
        this.entity=(QRobotEntity) entity;

        addSlotToContainer(new SlotItemHandler(this.entity.inventory_chip, 0, 20-8, 45-8));
        for(int i=0;i<this.entity.inventory_storage.getSlots();i++) {
            addSlotToContainer(new SlotItemHandler(this.entity.inventory_storage, i, offx+(i%9) * 18, (i/9) * 18+offy));
        }


        addPlayerSlot(player,8,97);

    }

    @Override
    public void dataRecv(BasePacket packet) {
        switch (packet.type){
            case "qrobot_start":
                entity.startChip();
                break;
            case "qrobot_stop":
                entity.stopChip();
                break;
        }
    }

    /*@Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if(!playerIn.world.isRemote){
            entity.resetChip();
        }
    }*/
}