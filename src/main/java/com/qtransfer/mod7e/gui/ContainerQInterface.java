package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.blocks.QTransBlock;
import com.qtransfer.mod7e.blocks.transfer.QuantumInterfaceEntity;
import com.qtransfer.mod7e.proxy.BasePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerQInterface extends ContainerBase implements ISyncable{
    QuantumInterfaceEntity tile;

    public ContainerQInterface(EntityPlayer player, TileEntity tile){
        super();
        int offy=50,offx=8;
        this.tile=(QuantumInterfaceEntity) tile;

        for(int i=0;i<this.tile.inventory_plugin.getSlots();i++) {
            addSlotToContainer(new SlotItemHandler(this.tile.inventory_plugin, i, offx+(i%9) * 18, (i/9) * 18+offy));
        }

        addPlayerSlot(player,offx,97);

    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if(!tile.getWorld().isRemote) {
            QTransBlock.updateQuantumNet(tile.getWorld(),tile.getPos()); //更新量子网络
        }
    }

    @Override
    public void dataRecv(BasePacket packet) {
        switch (packet.type){
            case "set_name":
                tile.name=packet.data;
                break;
        }
    }
}
