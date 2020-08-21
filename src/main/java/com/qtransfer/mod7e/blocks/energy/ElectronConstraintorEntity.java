package com.qtransfer.mod7e.blocks.energy;

import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.blocks.QTransTileEntity;
import com.qtransfer.mod7e.energy.CapabilityQEnergy;
import com.qtransfer.mod7e.energy.IQEUser;
import com.qtransfer.mod7e.energy.QEnergyUser;
import com.qtransfer.mod7e.utils.IOItemStackHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ElectronConstraintorEntity extends EnergyProviderEntity {
    public ItemStackHandler inventory_energy = new ItemStackHandler(9);
    public ItemStackHandler inventory_magnet = new ItemStackHandler(2);
    public int energy_tick=0;
    public int total_tick;
    public String res_name=null;

    @Override
    public void update() {
        super.update();

        if(world.isRemote)
            return;

        //输出能量
        long rest_energy=energyProvider.extractEnergy(energyProvider.getEnergy(),false);
        for(int i=0;i<inventory_energy.getSlots();i++){
            if(rest_energy<=0)
                break;
            ItemStack stack=inventory_energy.getStackInSlot(i);
            if(!stack.isEmpty() && stack.hasCapability(CapabilityQEnergy.QENERGY_USER,null)){
                IQEUser iuser=stack.getCapability(CapabilityQEnergy.QENERGY_USER, null);
                rest_energy=iuser.insertEnergy(rest_energy,false);
            }
        }
        energyProvider.insertEnergy(rest_energy,false);

        //发电.
        if(energy_tick>0){
            int energy_add=getIonizerEnergyCount();
            if(energyProvider.insertEnergy(energy_add,true)<=0) {
                energyProvider.insertEnergy(energy_add,false);
                energy_tick--;
            }
        } else {
            int level=0;
            if(!inventory_magnet.getStackInSlot(0).isEmpty() && (level=getMagnetLevel(Utils.getOreNames(inventory_magnet.getStackInSlot(0))))!=0){
                total_tick=energy_tick=level;
                inventory_magnet.extractItem(0,1,false);

                double tmp=Math.random();
                //System.out.println("pro "+tmp);
                if(tmp<0.7)
                    inventory_magnet.insertItem(1,new ItemStack(Item.getByNameOrId(res_name),1),false);
                syncToTrackingClients();
                markDirty();
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory_energy.deserializeNBT(tag.getCompoundTag("inventory_energy"));
        inventory_magnet.deserializeNBT(tag.getCompoundTag("inventory_magnet"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("inventory_energy", this.inventory_energy.serializeNBT());
        tag.setTag("inventory_magnet", this.inventory_magnet.serializeNBT());
        return super.writeToNBT(tag);
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new IOItemStackHandler(inventory_magnet,1));
        } else {
            return super.getCapability(cap, facing);
        }
    }

    public int getIonizerEnergyCount(){
        BlockPos tile_pos=getPos();
        int count=0;

        count+=canIonizerWork(tile_pos.add(1,0,0));
        count+=canIonizerWork(tile_pos.add(-1,0,0));
        count+=canIonizerWork(tile_pos.add(0,0,1));
        count+=canIonizerWork(tile_pos.add(0,0,-1));

        count+=canIonizerWork(tile_pos.add(1,0,1));
        count+=canIonizerWork(tile_pos.add(-1,0,1));
        count+=canIonizerWork(tile_pos.add(1,0,-1));
        count+=canIonizerWork(tile_pos.add(-1,0,-1));

        return count;
    }

    public int canIonizerWork(BlockPos blockPos){
        if((world.getTileEntity(blockPos) != null) && (world.getTileEntity(blockPos) instanceof AirIonizerEntity))
            return ((AirIonizerEntity) world.getTileEntity(blockPos)).canWork();
        return 0;
    }

    public int getMagnetLevel(String[] orenames){
        if(Utils.contain_str(orenames,"ingotIronMagnetic")){
            res_name="minecraft:iron_ingot";
            return 80;
        }else if(Utils.contain_str(orenames,"ingotNeodymiumMagnetic")){
            res_name="qtrans:ingot_neodymium";
            return 300;
        }
        else if(Utils.contain_str(orenames,"inverseEntropyMagnetic")){
            return 1000;
        }
        return 0;
    }

}
