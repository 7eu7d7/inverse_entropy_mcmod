package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.blocks.energy.ElectronConstraintorEntity;
import com.qtransfer.mod7e.blocks.energy.EnergyBoxEntity;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerEnergyBox extends ContainerBase{
    EnergyBoxEntity entity;
    final int row_count=3;

    public ContainerEnergyBox(EntityPlayer player, TileEntity tile) {
        super();
        entity = (EnergyBoxEntity) tile;

        int startx=100-27,starty=6,offset=0;

        //energy storager
        for(int i=0;i<entity.inventory_energy.getSlots();i++) {
            SlotItemHandler slh=new SlotItemHandler(entity.inventory_energy, i, startx+(offset%row_count)*18, starty+(offset/row_count)*18);
            addSlotToContainer(slh);
            offset++;
        }

        addPlayerSlot(player,8,97);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (IContainerListener listener : this.listeners)
        {
            if(listener instanceof EntityPlayerMP) {
                entity.syncToTrackingClients();
                entity.markDirty();
                //QNetworkManager.INSTANCE.sendPacketToPlayer(new BasePacket("container", "energy_sync", "" + entity.energyProvider.energy), (EntityPlayerMP) listener);
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        entity.updateEnergy();
    }
}
