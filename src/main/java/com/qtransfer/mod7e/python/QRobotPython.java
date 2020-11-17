package com.qtransfer.mod7e.python;

import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.entity.QRobotEntity;
import com.qtransfer.mod7e.transfer.GeneralStack;
import com.qtransfer.mod7e.transfer.IStorageable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class QRobotPython extends EntityPython {

    World world;
    QRobotEntity entity_robot;
    volatile boolean arrive=false;

    public QRobotPython(EntityLiving entity) {
        super(entity);
        this.world=entity.world;
        entity_robot=(QRobotEntity)entity;
    }

    public ItemStackPython takeItem_StorageAt(int slot,int amount){
        return new ItemStackPython(entity_robot.inventory_storage.extractItem(slot,amount,false));
    }
    public ItemStackPython putItem_StorageAt(int slot,ItemStackPython stack){
        return new ItemStackPython(entity_robot.inventory_storage.insertItem(slot, stack.stack,false));
    }

    public ItemStackPython takeItem_Storage(String name,int count){
        int meta=check_meta(name);
        ItemStack stack=(meta==-1?new ItemStack(Item.getByNameOrId(name),count):
                new ItemStack(Item.getByNameOrId(name.substring(0,meta)),count,Integer.parseInt(name.substring(meta+1))));
        if(!stack.isEmpty()) {
            return new ItemStackPython(Utils.takeItemStack(entity_robot.inventory_storage, stack, false));
        }else
            return new ItemStackPython(ItemStack.EMPTY);
    }

    public ItemStackPython putItem_Storage(ItemStackPython stack){
        stack.set(ItemHandlerHelper.insertItemStacked(entity_robot.inventory_storage, stack.stack, false));
        return stack;
    }

    public boolean harvestBlock(PosPython pos){
        BlockPos rbp=processPos(pos).toBlockPos();

        if(!pos_available(new PosPython(rbp)))
            return false;

        return world.destroyBlock(rbp,true);
    }

    public boolean placeBlock(ItemStackPython stack,PosPython pos){
        BlockPos rbp=processPos(pos).toBlockPos();

        if(!pos_available(new PosPython(rbp)))
            return false;

        if(stack.stack.getItem() instanceof ItemBlock){
            ItemBlock ib= (ItemBlock) stack.stack.getItem();
            Block blk=ib.getBlock();
            return ib.placeBlockAt(stack.stack,null,world,rbp,null,rbp.getX(),rbp.getY(),rbp.getZ(),blk.getStateFromMeta(stack.getMetadata()));
        }
        return false;
    }

    public boolean moveTo(PosPython pos){
        arrive=false;
        pos=processPos(pos);
        if(!det_available(pos))
            return false;
        if(!entity_robot.moveTo(pos.pos)) {
            System.out.println("cant move to:"+entity_robot.getNavigator().getPathSearchRange());
            return false;
        }
        while (!arrive){}
        arrive=false;
        return true;
    }

    public void jump(){
        entity_robot.getJumpHelper().doJump();
    }

    public boolean pos_available(PosPython blockPos){
        return entity.getDistance(blockPos.getX(),blockPos.getY(),blockPos.getZ())<=5.2;
    }

    public boolean det_available(PosPython blockPos){
        return entity.getDistance(blockPos.getX(),blockPos.getY(),blockPos.getZ())<=entity_robot.det_range;
    }

    public boolean storageFull(){
        for(int i=0;i<entity_robot.inventory_storage.getSlots();i++){
            if(entity_robot.inventory_storage.getStackInSlot(i).isEmpty())
                return false;
        }
        return true;
    }

    public void arriveTarget(){
        //System.out.println("arr at pos"+getPositionVector());
        arrive=true;
    }

}
