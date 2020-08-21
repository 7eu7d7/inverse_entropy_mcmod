package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.blocks.transfer.QuantumBufferEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerQBuffer extends ContainerBase{
    QuantumBufferEntity tile;

    public ContainerQBuffer(EntityPlayer player, TileEntity tile){
        super();
        int offy=30,offx=8;
        this.tile=(QuantumBufferEntity) tile;

        addSlotToContainer(new SlotItemHandler(this.tile.inventory_storage, 0, 80, 45-8));

        addPlayerSlot(player,offx,97);

    }
}
