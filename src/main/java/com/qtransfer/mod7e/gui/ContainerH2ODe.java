package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.blocks.H2ODecomposerEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerH2ODe extends ContainerBase {

    H2ODecomposerEntity tile;

    public ContainerH2ODe(EntityPlayer player, TileEntity tile){
        super();
        int offy=50;
        this.tile=(H2ODecomposerEntity) tile;

        addSlotToContainer(new SlotItemHandler(this.tile.inventory_itemin, 0, 55+2, 35+2));

        addSlotToContainer(new SlotItemHandler(this.tile.inventory_in, 0, 5+2, 15+2));
        addSlotToContainer(new SlotItemHandler(this.tile.inventory_in, 1, 80+2, 15+2));
        addSlotToContainer(new SlotItemHandler(this.tile.inventory_in, 2, 130+2, 15+2));

        addSlotToContainer(new SlotItemHandler(this.tile.inventory_out, 0, 5+2, 55+2));
        addSlotToContainer(new SlotItemHandler(this.tile.inventory_out, 1, 80+2, 55+2));
        addSlotToContainer(new SlotItemHandler(this.tile.inventory_out, 2, 130+2, 55+2));

        addPlayerSlot(player,8,97);

    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        return null;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (IContainerListener listener : this.listeners)
        {
            listener.sendWindowProperty(this, 0, tile.progress);

        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data)
    {
        super.updateProgressBar(id, data);

        switch (id)
        {
            case 0:
                tile.progress = data;
                break;
            default:
                break;
        }
    }
}
