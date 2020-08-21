package com.qtransfer.mod7e;

import com.qtransfer.mod7e.blocks.*;
import com.qtransfer.mod7e.blocks.energy.AirIonizer;
import com.qtransfer.mod7e.blocks.energy.ElectronConstraintor;
import com.qtransfer.mod7e.blocks.energy.EnergyBox;
import com.qtransfer.mod7e.blocks.energy.EnergyConverter;
import com.qtransfer.mod7e.blocks.transfer.*;
import com.qtransfer.mod7e.items.FluidTankItem;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Vector;


public class BlockMaker {
    public static Vector<Block> blocklist=new Vector<Block>();
    public static Block air_ionizer,fluid_tank;

    public BlockMaker(){
        addBlock(new H2ODecomposer(),"h2o_decmp");
        addBlock(new Pipe(),"quantum_pipe");
        addBlock(new WaveStabilizer(),"wave_stabilizer");
        addBlock(new QuantumInterface(),"quantum_interface");
        addBlock(new QuantumBuffer(),"quantum_buffer");
        addBlock(new AutoCrafter(),"auto_crafter");
        addBlock(air_ionizer=new AirIonizer(),"air_ionizer");
        addBlock(new ElectronConstraintor(),"electron_constraintor");
        addBlock(new EnergyConverter(),"energy_converter");
        addBlock(new BlockShaper(),"block_shaper");
        addBlock(new QuantumChest(),"quantum_chest");
        addBlock(new EnergyBox(),"energy_box");

        ItemMaker.addItem(new ItemBlock(addBlockEXItem(fluid_tank=new BlockFluidTank(),"fluid_tank")){
            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                FluidTankItem fti=new FluidTankItem(stack);
                tooltip.add(Utils.getShowNum(fti.tank.getFluidAmount())+"/"+Utils.getShowNum(fti.tank.getCapacity()));
            }

            @Nullable
            @Override
            public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
                return new ICapabilityProvider() {
                    @Override
                    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
                        return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
                    }
                    @Override
                    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
                        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new FluidTankItem(stack));
                        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
                            return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(new FluidTankItem(stack));
                        } else {
                            return null;
                        }
                    }
                };
            }
        },"fluid_tank");
    }

    public static void addBlock(Block block,String name){
        block.setRegistryName(name).setUnlocalizedName(name).setCreativeTab(TabsList.Quantum_Transfer_TAB);
        blocklist.add(block);
        ItemMaker.addItem(new ItemBlock(block),name);
    }

    public static Block addBlockEXItem(Block block,String name){
        block.setRegistryName(name).setUnlocalizedName(name).setCreativeTab(TabsList.Quantum_Transfer_TAB);
        blocklist.add(block);
        return block;
    }
}
