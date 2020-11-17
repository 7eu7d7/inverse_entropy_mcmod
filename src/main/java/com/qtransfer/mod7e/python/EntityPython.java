package com.qtransfer.mod7e.python;

import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.transfer.GeneralStack;
import com.qtransfer.mod7e.transfer.IStorageable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class EntityPython extends CommonPython{
    protected EntityLiving entity;
    protected int pos_mode=1;

    public static final int ABS_MODE=1;
    public static final int REL_MODE=2;

    public EntityPython(EntityLiving entity){
        this.entity=entity;
    }

    public String getBlockName(PosPython pos){
        return entity.world.getBlockState(processPos(pos).toBlockPos()).getBlock().getRegistryName().toString();
    }

    public PosPython getPos(){
        return new PosPython(entity.getPositionVector());
    }

    public PosPython processPos(PosPython pos){
        if(pos_mode==ABS_MODE)
            return pos;
        else if(pos_mode==REL_MODE)
            return pos.add((int)entity.posX,(int)entity.posY,(int)entity.posZ);
        return pos;
    }

    public EnumFacing findItemStorage(){
        for(EnumFacing face:EnumFacing.VALUES){
            if(entity.world.getTileEntity(entity.getPosition().offset(face))!=null){
                return face;
            }
        }
        return null;
    }

    public ItemStackPython putItems(ItemStackPython stack){
        return putItems(findItemStorage(),stack);
    }
    public ItemStackPython takeItems(String name,int count){
        return takeItems(findItemStorage(),name,count);
    }

    public ItemStackPython putItems(EnumFacing facing, ItemStackPython stack){
        TileEntity tmp=null;
        if((tmp=entity.world.getTileEntity(entity.getPosition().offset(facing)))!=null && tmp instanceof IStorageable){

            stack.set(((IStorageable)tmp).addItem(new GeneralStack(stack.stack),true).stack);
            return stack;
        }else {
            stack.set(ItemHandlerHelper.insertItemStacked(getItemCapacity(facing), stack.stack, false));
            return stack;
        }
    }

    public ItemStackPython takeItems(EnumFacing facing,String name,int count){
        int meta=check_meta(name);
        ItemStack stack=(meta==-1?new ItemStack(Item.getByNameOrId(name),count):
                new ItemStack(Item.getByNameOrId(name.substring(0,meta)),count,Integer.parseInt(name.substring(meta+1))));

        //ItemStack stack=new ItemStack(Item.getByNameOrId(name),count);
        if(!stack.isEmpty()) {
            TileEntity tmp=null;
            if((tmp=entity.world.getTileEntity(entity.getPosition().offset(facing)))!=null && tmp instanceof IStorageable){
                return new ItemStackPython(((IStorageable)tmp).takeItem(new GeneralStack(stack),count,true).stack);
            }else
                return new ItemStackPython(Utils.takeItemStack(getItemCapacity(facing), stack, false));
        }else
            return new ItemStackPython(ItemStack.EMPTY);
    }

    public void setPosMode(int pos_mode) {
        this.pos_mode = pos_mode;
    }

    public IItemHandler getItemCapacity(EnumFacing facing){
        return getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,facing);
    }

    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        TileEntity tmp=null;
        if((tmp=entity.world.getTileEntity(entity.getPosition().offset(facing)))!=null){
            return tmp.getCapability(capability, facing.getOpposite());
        }
        return null;
    }
}
