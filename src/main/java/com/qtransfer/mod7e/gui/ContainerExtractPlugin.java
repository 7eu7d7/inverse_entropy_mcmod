package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.items.CraftPluginItem;
import com.qtransfer.mod7e.items.ExtractPluginItem;
import com.qtransfer.mod7e.proxy.BasePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class ContainerExtractPlugin extends ContainerBase implements ISyncable{
    ExtractPluginItem extract;
    final int row_count=9;

    public ContainerExtractPlugin(EntityPlayer player){
        super();
        extract=new ExtractPluginItem(player.getHeldItem(EnumHand.MAIN_HAND));

        int offset=0;
        int startx=9,starty=6;

        //Item
        starty+=60;
        for(int i=0;i<extract.inventory_item.getSlots();i++) {
            SlotFake slh=new SlotFake(extract.inventory_item, i, startx+(offset%row_count)*18, starty+(offset/row_count)*18);
            addSlotToContainer(slh);
            offset++;
        }

        //Fluid
        starty+=20+5;
        offset=0;
        for(int i=0;i<extract.inventory_fluid.size();i++) {
            FluidSlotFake slh=new FluidSlotFake(extract.inventory_fluid.get(i), startx+(offset%row_count)*18, starty+(offset/row_count)*18);
            addFluidSlot(slh);
            offset++;
        }

        addPlayerSlot(player,8,134);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        extract.writeNBT();
    }

    @Override
    public void dataRecv(BasePacket packet) {
        switch (packet.type){
            case "set_name":
                extract.target=packet.data;
                break;
        }
    }
}
