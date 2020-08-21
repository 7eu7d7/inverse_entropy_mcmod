package com.qtransfer.mod7e.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotFake extends SlotItemHandler {
    boolean enable=true;
    public SlotFake(IItemHandler inv, int slotNumber, int xPos, int yPos) {
        super(inv, slotNumber, xPos, yPos);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}
