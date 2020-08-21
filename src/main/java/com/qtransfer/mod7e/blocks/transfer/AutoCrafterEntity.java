package com.qtransfer.mod7e.blocks.transfer;

import com.qtransfer.mod7e.blocks.QTransTileEntity;
import com.qtransfer.mod7e.transfer.ITiggerable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class AutoCrafterEntity extends QTransTileEntity implements ITiggerable,IItemHandler{
    public InventoryCrafting crafter=new InventoryCrafting(new Container() {
        @Override
        public boolean canInteractWith(@Nonnull final EntityPlayer playerIn) {
            return false;
        }
    }, 3, 3);
    public ItemStackHandler inventory_craft = new ItemStackHandler(9);
    public ItemStackHandler inventory_out = new ItemStackHandler(1);

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.inventory_craft.deserializeNBT(tag.getCompoundTag("inventory_craft"));
        this.inventory_out.deserializeNBT(tag.getCompoundTag("inventory_out"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("inventory_craft", this.inventory_craft.serializeNBT());
        tag.setTag("inventory_out", this.inventory_out.serializeNBT());
        super.writeToNBT(tag);
        return tag;
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this);
        } else {
            return super.getCapability(cap, facing);
        }
    }

    @Override
    public void tigger() {
        for (int i=0;i<inventory_craft.getSlots();i++) {
            crafter.setInventorySlotContents(i,inventory_craft.extractItem(i,inventory_craft.getStackInSlot(i).getCount(),false));
        }
        IRecipe recp=CraftingManager.findMatchingRecipe(crafter,world);
        if (recp != null) {
            inventory_out.insertItem(0,recp.getCraftingResult(crafter).copy(),false);
        }
    }

    @Override
    public int getSlots() {
        return inventory_craft.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot>=1?ItemStack.EMPTY:inventory_out.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return inventory_craft.insertItem(slot,stack,simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return slot>=1?ItemStack.EMPTY:inventory_out.extractItem(slot,amount,simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory_craft.getSlotLimit(slot);
    }
}
