package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.blocks.BlockShaperEntity;
import com.qtransfer.mod7e.blocks.transfer.QuantumBufferEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerBlockShaper extends ContainerBase{
    BlockShaperEntity tile;

    public ContainerBlockShaper(EntityPlayer player, TileEntity tile){
        super();
        int offy=30,offx=8;
        this.tile=(BlockShaperEntity) tile;

        addSlotToContainer(new SlotItemHandler(this.tile.inventory_chip, 0, 80, 45-8));

        addPlayerSlot(player,offx,97);

    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if(!playerIn.world.isRemote){
            tile.resetChip();
        }
    }
}
