package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.blocks.energy.AirIonizer;
import com.qtransfer.mod7e.blocks.energy.AirIonizerEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerAirIonizer extends ContainerBase{
    AirIonizerEntity entity;

    public ContainerAirIonizer(EntityPlayer player, TileEntity tile){
        super();
        int offy=30,offx=8;
        entity=(AirIonizerEntity) tile;
        addSlotToContainer(new SlotItemHandler(entity.inventory, 0, 80, 45-8));
        addPlayerSlot(player,offx,97);

    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        //playerIn.world.getBlockState(entity.getPos()).withProperty(AirIonizer.LIT,entity.canWork());
        //entity.syncToTrackingClients();
        playerIn.world.setBlockState(entity.getPos(),playerIn.world.getBlockState(entity.getPos()).withProperty(AirIonizer.LIT,entity.canWork()>0),0);
    }
}
