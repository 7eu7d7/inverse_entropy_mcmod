package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.items.StoragePluginItem;
import com.qtransfer.mod7e.proxy.BasePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

public class ContainerStoragePlugin extends ContainerBase implements ISyncable{
    StoragePluginItem spi;

    public ContainerStoragePlugin(EntityPlayer player){
        super();
        spi=new StoragePluginItem(player.getHeldItem(EnumHand.MAIN_HAND));
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (playerIn.isServerWorld()) {
            spi.writeNBT();
        }
    }

    @Override
    public void dataRecv(BasePacket packet) {
        switch (packet.type){
            case "face":
                spi.setFace(EnumFacing.byName(packet.data));
                break;
        }
    }
}
