package com.qtransfer.mod7e.python;

import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.blocks.transfer.QuantumInterfaceEntity;
import com.qtransfer.mod7e.transfer.GeneralStack;
import com.qtransfer.mod7e.transfer.IStorageable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class TileEntityPython extends CommonPython{
    public World world;
    public BlockPos pos;
    public TileEntity entity;

    public TileEntityPython(TileEntity entity){
        world=entity.getWorld();
        pos=entity.getPos();
        this.entity=entity;
    }

    public IItemHandler getItemCapacity(EnumFacing facing){
        return getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,facing);
    }

    public IFluidHandler getFluidCapacity(EnumFacing facing){
        return getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,facing);
    }

    public ItemStackPython putItems(EnumFacing facing,ItemStackPython stack){
        TileEntity tmp=null;
        if((tmp=world.getTileEntity(pos.offset(facing)))!=null && tmp instanceof IStorageable){
            stack.set(((IStorageable)tmp).addItem(new GeneralStack(stack.stack),true).stack);
            return stack;
        }else {
            stack.set(ItemHandlerHelper.insertItemStacked(getItemCapacity(facing), stack.stack, false));
            return stack;
        }
    }

    public ItemStackPython takeItems(EnumFacing facing,String name,int count){
        ItemStack stack=new ItemStack(Item.getByNameOrId(name),count);
        if(!stack.isEmpty()) {
            TileEntity tmp=null;
            if((tmp=world.getTileEntity(pos.offset(facing)))!=null && tmp instanceof IStorageable){
                return new ItemStackPython(((IStorageable)tmp).takeItem(new GeneralStack(stack),count,true).stack);
            }else
                return new ItemStackPython(Utils.takeItemStack(getItemCapacity(facing), stack, false));
        }else
            return new ItemStackPython(ItemStack.EMPTY);
    }

    public FluidStackPython putFluid(EnumFacing facing,FluidStackPython stack){
        TileEntity tmp=null;
        if((tmp=world.getTileEntity(pos.offset(facing)))!=null && tmp instanceof IStorageable){
            return new FluidStackPython(((IStorageable)tmp).addItem(new GeneralStack(stack.stack),true).fstack);
        }else
            return new FluidStackPython(Utils.fluidFill(getFluidCapacity(facing),stack.stack));
    }

    public FluidStackPython takeFluid(EnumFacing facing,String name,int count){
        FluidStack stack=new FluidStack(FluidRegistry.getFluid(name),count);
        if(stack!=null) {
            TileEntity tmp=null;
            if((tmp=world.getTileEntity(pos.offset(facing)))!=null && tmp instanceof IStorageable){
                return new FluidStackPython(((IStorageable)tmp).takeItem(new GeneralStack(stack),count,true).fstack);
            }else
                return new FluidStackPython(getFluidCapacity(facing).drain(stack, true));
        }else
            return new FluidStackPython(null);
    }

    public String getBlockName(PosPython pos){
        return world.getBlockState(pos.toBlockPos()).getBlock().getRegistryName().toString();
    }

    public PosPython getPos(){
        return new PosPython(pos);
    }

    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        TileEntity tmp=null;
        if((tmp=world.getTileEntity(pos.offset(facing)))!=null){
            return tmp.getCapability(capability, facing.getOpposite());
        }
        return null;
    }

}
