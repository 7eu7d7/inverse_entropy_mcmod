package com.qtransfer.mod7e.blocks;

import com.qtransfer.mod7e.items.SingleChipItem;
import com.qtransfer.mod7e.python.BlockShaperPython;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;

public class BlockShaperEntity extends QTransTileEntity implements ITickable{
    public ItemStackHandler inventory_chip = new ItemStackHandler(1);
    public SingleChipItem chip;
    public boolean start_run=true;

    Runnable task=new Runnable(){
        @Override
        public void run() {
            if (chip != null) {
                chip.script.callFunction("main",new BlockShaperPython(BlockShaperEntity.this));
            }
        }
    };
    Thread task_run;


    @Override
    public void update() {
        if(world.isRemote)
            return;

        if (start_run && task_run == null) {
            task_run=new Thread(task);
            task_run.start();
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
        chip.stopRunning();
        chip=new SingleChipItem(inventory_chip.getStackInSlot(0));
        chip.initScript();
    }
}
