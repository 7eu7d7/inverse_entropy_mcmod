package com.qtransfer.mod7e.items;

import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.energy.CapabilityQEnergy;
import com.qtransfer.mod7e.energy.GeneralEnergy;
import com.qtransfer.mod7e.energy.QEnergyUser;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class QuantumSuperpositionBall implements INBTSerializable<NBTTagCompound> {

    public QEnergyUser energy=new QEnergyUser(1L<<50){
        @Override
        public long insertEnergy(long energy, boolean simulate) {
            long eng = super.insertEnergy(energy, simulate);
            writeNBT();
            return eng;
        }

        @Override
        public long extractEnergy(long energy, boolean simulate) {
            long eng = super.extractEnergy(energy, simulate);
            writeNBT();
            return eng;
        }
    };

    public EnergyStorage fe_energy=new EnergyStorage(Integer.MAX_VALUE){
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int eng_rst=(int)QuantumSuperpositionBall.this.energy.insertEnergy(maxReceive/4,simulate)*4;
            return maxReceive-eng_rst;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return (int)QuantumSuperpositionBall.this.energy.extractEnergy(maxExtract/4,simulate)*4;
        }

        @Override
        public int getEnergyStored() {
            long eng=QuantumSuperpositionBall.this.energy.getEnergy();
            return (int) (eng*4>=Integer.MAX_VALUE?Integer.MAX_VALUE:eng*4);
        }
    };

    boolean outfe=false;

    ItemStack stack;

    public QuantumSuperpositionBall(ItemStack stack){
        energy.setExtractSpeed(1L<<20);
        energy.setInsertSpeed(1L<<20);
        this.stack=stack;
        deserializeNBT(stack.getTagCompound());
    }

    public void writeNBT(){
        stack.setTagCompound(serializeNBT());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        nbt.setTag("energy",energy.serializeNBT());
        nbt.setBoolean("outfe",outfe);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt==null)
            return;
        energy.deserializeNBT(nbt.getCompoundTag("energy"));
        outfe=nbt.getBoolean("outfe");
    }

    public static class Register implements IItemRegister{
        @Override
        public Item registItem() {
            return new Item(){

                @SideOnly(Side.CLIENT)
                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                    QuantumSuperpositionBall qsb=new QuantumSuperpositionBall(stack);
                    if(!stack.hasTagCompound())
                        qsb.writeNBT();
                    tooltip.add(Utils.getShowNum(qsb.energy.getEnergy())+"/"+Utils.getShowNum(qsb.energy.getCapacity()));
                    NBTTagCompound nbt=stack.getTagCompound();
                    if(nbt.getBoolean("outfe"))
                        tooltip.add("energy convert upgrade");
                }

                @Override
                public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
                    return new ICapabilityProvider() {
                        @Override
                        public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
                            NBTTagCompound nbt=stack.getTagCompound();
                            return cap == CapabilityQEnergy.QENERGY_USER || (cap == CapabilityEnergy.ENERGY && nbt!=null && nbt.getBoolean("outfe"));
                        }
                        @Override
                        public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
                            NBTTagCompound nbt=stack.getTagCompound();
                            if (cap == CapabilityQEnergy.QENERGY_USER) {
                                return CapabilityQEnergy.QENERGY_USER.cast(new QuantumSuperpositionBall(stack).energy);
                            } else if(cap == CapabilityEnergy.ENERGY && nbt!=null && nbt.getBoolean("outfe")){
                                return CapabilityEnergy.ENERGY.cast(new QuantumSuperpositionBall(stack).fe_energy);
                            } else{
                                return null;
                            }
                        }
                    };
                }
            };
        }

        @Override
        public String getRegistName() {
            return "quantum_superposition_ball";
        }
    }
}
