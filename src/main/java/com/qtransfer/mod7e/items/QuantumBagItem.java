package com.qtransfer.mod7e.items;

import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.energy.CapabilityQEnergy;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import com.qtransfer.mod7e.transfer.GeneralStack;
import com.qtransfer.mod7e.transfer.IStorageable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuantumBagItem implements INBTSerializable<NBTTagCompound>, IStorageable, IItemHandler, IFluidHandler {
    ItemStack stack_storage;

    public ItemStackHandler inventory_storage = new ItemStackHandler(1);
    public StorageItem[] storage=new StorageItem[1];
    HashMap<GeneralStack, Integer> storage_map=new HashMap<GeneralStack, Integer>();

    public boolean change_mark=false;

    public QuantumBagItem(ItemStack stack){
        stack_storage=stack;
        deserializeNBT(stack.getTagCompound());
        updateStorage();
    }

    public void deserializeStroage(NBTTagCompound tag) {
        for(int i=0;i<storage.length;i++) {
            storage[i] = new StorageItem(inventory_storage.getStackInSlot(i));
            if (storage[i].isStorage() && tag.hasKey("storage"+i)) {
                storage[i].deserializeNBT(tag.getCompoundTag("storage"+i));
            }
        }
    }

    public NBTTagCompound serializeStroage() {
        NBTTagCompound nbt=new NBTTagCompound();
        for(int i=0;i<storage.length;i++) {
            if (storage[i]!=null && storage[i].isStorage()) {
                storage[i].saveNBT();
                nbt.setTag("storage"+i, storage[i].serializeNBT());
            }
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        if(compound==null)
            return;
        deserializeStroage(compound.getCompoundTag("storage_list"));
        inventory_storage.deserializeNBT(compound.getCompoundTag("inventory"));
        //if(!world.isRemote)
        updateStorage();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound=new NBTTagCompound();
        compound.setTag("storage_list",serializeStroage());
        compound.setTag("inventory", inventory_storage.serializeNBT());
        return compound;
    }

    public void writeNBT(){
        stack_storage.setTagCompound(serializeNBT());
    }

    @Override
    public HashMap<GeneralStack, Integer> getStorgeMap() {
        /*HashMap<GeneralStack, Integer> stmap=new HashMap<GeneralStack, Integer>();
        for(StorageItem si:storage){
            HashMap<GeneralStack, Integer> itemmap=si.getStorgeMap();
            for(Map.Entry<GeneralStack, Integer> entry:itemmap.entrySet()){
                if(!stmap.containsKey(entry.getKey()))
                    stmap.put(entry.getKey(),0);
                stmap.put(entry.getKey(),stmap.get(entry.getKey())+entry.getValue());
            }
        }*/
        return storage_map;
    }

    public int getMaxSize(){
        int size=0;
        for(StorageItem si:storage){
            size+=si.getMaxSize();
        }
        return size;
    }

    public int getUsedSize(){
        int size=0;
        for(StorageItem si:storage){
            size+=si.getUsedSize();
        }
        return size;
    }

    public void updateStorage(){
        for(int i=0;i<storage.length;i++)
            storage[i]=new StorageItem(inventory_storage.getStackInSlot(i));

        storage_map.clear();
        for(StorageItem si:storage){
            HashMap<GeneralStack, Integer> itemmap=si.getStorgeMap();
            for(Map.Entry<GeneralStack, Integer> entry:itemmap.entrySet()){
                if(!storage_map.containsKey(entry.getKey()))
                    storage_map.put(entry.getKey(),0);
                storage_map.put(entry.getKey(),storage_map.get(entry.getKey())+entry.getValue());
            }
        }
    }

    //ItemHandler
    @Override
    public boolean hasItem(GeneralStack itemlist) {
        return storage_map.containsKey(itemlist);
    }

    @Override
    public int getItemCount(GeneralStack itemlist) {
        return storage_map.get(itemlist);
    }

    @Override
    public GeneralStack addItem(GeneralStack items, boolean doadd) {
        GeneralStack res=items.copy();
        for(StorageItem si:storage){
            res=si.addItem(res, doadd);
        }

        //更新storage map
        if(doadd) {
            if (!storage_map.containsKey(items))
                storage_map.put(items, items.getCount() - res.getCount());
            else
                storage_map.put(items, storage_map.get(items) + items.getCount() - res.getCount());
            change_mark=true;
        }
        return res;
    }

    @Override
    public GeneralStack takeItem(GeneralStack items, int count, boolean dotake) {
        if(!storage_map.containsKey(items))
            return items.getEmpty();

        GeneralStack res=items.copy();
        res.setCount(0);
        for(StorageItem si:storage){
            GeneralStack take=si.takeItem(items,count,dotake);
            count-=take.getCount();
            res.setCount(res.getCount()+take.getCount());
            if(count<=0)
                break;
        }

        //更新storage map
        if(dotake) {
            storage_map.put(items, storage_map.get(items) - res.getCount());
            if (storage_map.get(items) <= 0)
                storage_map.remove(items);
            change_mark=true;
        }
        return res;
    }

    @Override
    public void saveNBT() {
        writeNBT();
    }

    @Override
    public int getSlots() {
        return storage_map.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if(slot>=getSlots())
            return ItemStack.EMPTY;
        GeneralStack[] list=storage_map.keySet().toArray(new GeneralStack[0]);
        if(list[slot].fluid)
            return ItemStack.EMPTY;
        else {
            GeneralStack stack=list[slot].copy();
            stack.setCount(storage_map.get(stack));
            return stack.stack;
        }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return addItem(new GeneralStack(stack), !simulate).stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if(slot>=getSlots())
            return ItemStack.EMPTY;
        GeneralStack[] list=storage_map.keySet().toArray(new GeneralStack[0]);
        if(list[slot].fluid)
            return ItemStack.EMPTY;
        else {
            GeneralStack stack=list[slot].copy();
            return takeItem(stack,amount,!simulate).stack;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    //FluidHanlder
    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[0];
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return resource.amount-addItem(new GeneralStack(resource),doFill).getCount();
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return takeItem(new GeneralStack(resource),resource.amount,doDrain).fstack;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        for(GeneralStack stack:storage_map.keySet()){
            if(stack.fluid)
                return takeItem(new GeneralStack(stack.fstack),maxDrain,doDrain).fstack;
        }
        return null;
    }

    public static class Register implements IItemRegister{
        @Override
        public Item registItem() {
            return new Item(){
                @Override
                public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                    if(!worldIn.isRemote) {
                        if (Keyboard.isKeyDown(42)) {
                            playerIn.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_Q_BAG, worldIn, 0, 0, 0);
                        } else {
                            playerIn.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_QSTORAGE_ITEM, worldIn, 0, 0, 0);
                        }
                    }
                    return super.onItemRightClick(worldIn, playerIn, handIn);
                }

                @SideOnly(Side.CLIENT)
                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                    QuantumBagItem qbag=new QuantumBagItem(stack);
                    if(!stack.hasTagCompound())
                        qbag.writeNBT();
                }

                @Override
                public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
                    return new ICapabilityProvider() {
                        public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
                            return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
                        }

                        @Override
                        public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
                            if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new QuantumBagItem(stack));
                            } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new QuantumBagItem(stack));
                            } else {
                                return null;
                            }
                        }
                    };
                }
            };
        }

        @Override
        public String getRegistName() {
            return "quantum_bag";
        }
    }
}
