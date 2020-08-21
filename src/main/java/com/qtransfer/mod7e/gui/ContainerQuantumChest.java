package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.blocks.QuantumChestEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerQuantumChest extends ContainerBase{
    QuantumChestEntity tile;

    public ContainerQuantumChest(EntityPlayer player, TileEntity tile){
        super();
        int offy=30,offx=8;
        this.tile=(QuantumChestEntity) tile;

        int startx=100-27,starty=6,offset=0;

        //stuffs
        int row_count=3;
        for(int i=0;i<this.tile.inventory_storage.getSlots();i++) {
            SlotItemHandler slh=new SlotItemHandler(this.tile.inventory_storage, i, startx+(offset%row_count)*18, starty+(offset/row_count)*18);
            addSlotToContainer(slh);
            offset++;
        }

        addPlayerSlot(player,offx,97);

    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if(!tile.getWorld().isRemote) {
            tile.updateStorage();
            tile.markDirty();
        }
    }
}
