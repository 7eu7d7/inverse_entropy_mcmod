package com.qtransfer.mod7e.blocks;

import com.qtransfer.mod7e.items.SingleChipItem;
import com.qtransfer.mod7e.python.BlockShaperPython;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;

public class BlockShaperEntity extends QTransTileEntity implements ITickable{
    public ItemStackHandler inventory_chip = new ItemStackHandler(1);
    public SingleChipItem chip;

    @Override
    public void update() {
        if(world.isRemote)
            return;

        if (chip != null) {
            chip.script.callFunction("tick",new BlockShaperPython(this));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.inventory_chip.deserializeNBT(tag.getCompoundTag("inventory_chip"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("inventory_chip", this.inventory_chip.serializeNBT());
        super.writeToNBT(tag);
        return tag;
    }

    public void resetChip(){
        chip=new SingleChipItem(inventory_chip.getStackInSlot(0));
        chip.initScript();
    }
}
