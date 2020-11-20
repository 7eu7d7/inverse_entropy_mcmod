package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.blocks.QuantumChestEntity;
import com.qtransfer.mod7e.items.QuantumBagItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerQuantumBag extends ContainerBase{
    QuantumBagItem bag;

    public ContainerQuantumBag(EntityPlayer player){
        super();
        int offy=30,offx=8;
        this.bag=new QuantumBagItem(player.getHeldItem(EnumHand.MAIN_HAND));

        int startx=100-27,starty=6,offset=0;

        //stuffs
        int row_count=3;
        for(int i=0;i<this.bag.inventory_storage.getSlots();i++) {
            SlotItemHandler slh=new SlotItemHandler(this.bag.inventory_storage, i, startx+(offset%row_count)*18, starty+(offset/row_count)*18);
            addSlotToContainer(slh);
            offset++;
        }

        addPlayerSlot(player,offx,97);

    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if(!playerIn.world.isRemote) {
            bag.updateStorage();
            bag.saveNBT();
        }
    }
}
