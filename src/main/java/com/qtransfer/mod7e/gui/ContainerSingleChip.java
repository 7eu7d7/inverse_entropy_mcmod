package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.items.SingleChipItem;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;

public class ContainerSingleChip extends ContainerBase implements ISyncable{
    SingleChipItem chip;
    EntityPlayer player;
    GuiSingleChip gui_chip;

    public ContainerSingleChip(EntityPlayer player){
        super();
        this.player=player;
        chip=new SingleChipItem(player.getHeldItem(EnumHand.MAIN_HAND));
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        chip.writeNBT();
    }

    @Override
    public void dataRecv(BasePacket packet) {
        switch (packet.type){
            case "code_save":
                //System.out.println("save "+packet.data);
                chip.saveToFile(SingleChipItem.file_name, packet.data);
                break;
            case "code_get":
                //同步至客户端
                QNetworkManager.INSTANCE.sendPacketToPlayer(new BasePacket("container","code_sync",chip.readFromFile(SingleChipItem.file_name)) , (EntityPlayerMP) player);
                break;
            case "code_sync":
                if(gui_chip!=null) {
                    gui_chip.code_view.setText(packet.data);
                    gui_chip.initok=true;
                }
                break;
        }
    }
}
