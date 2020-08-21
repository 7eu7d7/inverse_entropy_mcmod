package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.items.AdvanceCraftPlugin;
import com.qtransfer.mod7e.proxy.BasePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class ContainerAdvanceCP extends ContainerCraftPlugin{
    AdvanceCraftPlugin acp;
    EntityPlayer player;

    public ContainerAdvanceCP(EntityPlayer player) {
        this.player=player;
        acp=new AdvanceCraftPlugin(player.getHeldItem(EnumHand.MAIN_HAND));
        craft=acp;
        init(player);
    }

    public void setSendName(String name,int which){
        if(which>=100){
            acp.fluid_send.set(which-100,name);
        } else {
            acp.item_send.set(which,name);
        }
    }

    @Override
    public void dataRecv(BasePacket packet) {
        switch (packet.type){
            case "set_send_name":
                String[] strs=packet.data.split(",");
                setSendName(strs[0],Integer.parseInt(strs[1]));
                break;
            default:
                super.dataRecv(packet);
                break;
        }

    }
}
