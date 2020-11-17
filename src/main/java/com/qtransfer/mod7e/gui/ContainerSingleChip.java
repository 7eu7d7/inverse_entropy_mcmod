package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.gui.sigchip.GuiSingleChip;
import com.qtransfer.mod7e.gui.sigchip.GuiSingleChipList;
import com.qtransfer.mod7e.items.SingleChipItem;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;

import java.io.File;

public class ContainerSingleChip extends ContainerBase implements ISyncable{
    SingleChipItem chip;
    EntityPlayer player;
    public GuiSingleChip gui_chip;
    public GuiSingleChipList gui_chip_list;

    public ContainerSingleChip(EntityPlayer player){
        super();
        this.player=player;
        if(!player.world.isRemote)
            chip=new SingleChipItem(player.getHeldItem(EnumHand.MAIN_HAND));
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if(chip!=null)
            chip.writeNBT();
    }

    @Override
    public void dataRecv(BasePacket packet) {
        switch (packet.type){
            case "files_get":
                //同步至客户端
                QNetworkManager.INSTANCE.sendPacketToPlayer(new BasePacket("container","files_sync", Utils.list2str(chip.getAllScripts())) , (EntityPlayerMP) player);
                break;
            case "files_sync":
                if(gui_chip_list!=null) {
                    gui_chip_list.code_list.items.clear();
                    for(String file:packet.data.split(";")) {
                        gui_chip_list.code_list.addItem(GuiElementLoader.getIcon(file), file);
                    }
                }
                break;
            case "add_file":
                chip.createFile(packet.data);
                break;

            case "select_file":
                chip.file_name=packet.data;
                break;
            case "code_save":
                //System.out.println("save "+packet.data);
                chip.saveToFile(chip.file_name, packet.data);
                break;
            case "code_get":
                //同步至客户端
                QNetworkManager.INSTANCE.sendPacketToPlayer(new BasePacket("container","code_sync",chip.readFromFile(chip.file_name)) , (EntityPlayerMP) player);
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
