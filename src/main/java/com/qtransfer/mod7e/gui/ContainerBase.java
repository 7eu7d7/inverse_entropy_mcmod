package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.proxy.BasePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class ContainerBase extends Container{
    List<FluidSlot> fslots=new ArrayList<FluidSlot>();

    public void addPlayerSlot(EntityPlayer player, int ofx, int ofy){
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9,  ofx+j * 18, i * 18+ofy));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(player.inventory, i, ofx + i * 18, 58+ofy));
        }
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if(player.world.isRemote && slotId>=0 && slotId<this.inventorySlots.size() && this.inventorySlots.get(slotId) instanceof SlotFake && this.inventorySlots.get(slotId).isEnabled()){
            ItemStack hold_stack=player.inventory.getItemStack().copy();
            SlotFake slot=(SlotFake)this.inventorySlots.get(slotId);

            if(hold_stack==ItemStack.EMPTY){
                slot.putStack(ItemStack.EMPTY);
            } else {
                if (ItemStack.areItemsEqual(hold_stack, slot.getStack())) {
                    slot.getStack().grow(dragType==0?hold_stack.getCount():1);
                } else {
                    if(dragType!=0){
                        ItemStack tmp=hold_stack.copy();
                        tmp.setCount(1);
                        slot.putStack(tmp);
                    }else
                        slot.putStack(hold_stack.copy());
                }
            }

            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    public void addFluidSlot(FluidSlot fslot){
        fslots.add(fslot);
    }

    public void syncFluidSlot(BasePacket packet){
        try {
            System.out.println("sync slot"+packet.data);
            NBTTagCompound nbt= JsonToNBT.getTagFromJson(packet.data);
            FluidStack stack=FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluid"));
            fslots.get(nbt.getInteger("index")).tank.setFluid(stack.amount>0?stack:null);
        } catch (NBTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
        return ItemStack.EMPTY;
    }
}
