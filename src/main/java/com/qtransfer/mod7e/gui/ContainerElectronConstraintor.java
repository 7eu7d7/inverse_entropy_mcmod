package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.blocks.energy.ElectronConstraintorEntity;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerElectronConstraintor extends ContainerBase implements ISyncable {
    ElectronConstraintorEntity entity;
    final int row_count=3;

    public ContainerElectronConstraintor(EntityPlayer player, TileEntity tile){
        super();
        int offy=30,offx=8;
        entity=(ElectronConstraintorEntity) tile;

        int startx=100-27,starty=6,offset=0;

        //stuffs
        for(int i=0;i<entity.inventory_energy.getSlots();i++) {
            SlotItemHandler slh=new SlotItemHandler(entity.inventory_energy, i, startx+(offset%row_count)*18, starty+(offset/row_count)*18);
            addSlotToContainer(slh);
            offset++;
        }

        addSlotToContainer(new SlotItemHandler(entity.inventory_magnet, 0, 140, 20));
        addSlotToContainer(new SlotItemHandler(entity.inventory_magnet, 1, 140, 60));

        addPlayerSlot(player,offx,97);

    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (IContainerListener listener : this.listeners)
        {
            if(listener instanceof EntityPlayerMP) {
                listener.sendWindowProperty(this, 0, entity.energy_tick);
                listener.sendWindowProperty(this, 1, entity.total_tick);
                QNetworkManager.INSTANCE.sendPacketToPlayer(new BasePacket("container", "energy_sync", "" + entity.energyProvider.energy), (EntityPlayerMP) listener);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data)
    {
        super.updateProgressBar(id, data);

        switch (id)
        {
            case 0:
                entity.energy_tick = data;
                break;
            case 1:
                entity.total_tick = data;
                break;
            default:
                break;
        }
    }

    @Override
    public void dataRecv(BasePacket packet) {
        switch (packet.type){
            case "energy_sync":
                entity.energyProvider.energy=Long.parseLong(packet.data);
                break;
        }
    }
}
