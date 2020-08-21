package com.qtransfer.mod7e.python;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class BlockShaperPython extends EntityPython{
    private int avai_range=32;

    public BlockShaperPython(TileEntity entity) {
        super(entity);
    }

    public List<ItemStackPython> breakBlock(PosPython pos){
        if(!pos_available(pos))
            return null;

        //List<ItemStackPython> res = items(world.getBlockState(pos.pos).getBlock().getDrops(world,pos.pos,world.getBlockState(pos.pos),0));
        IBlockState state=world.getBlockState(pos.pos);
        List<ItemStack> stacks=new ArrayList<ItemStack>();
        stacks.add(new ItemStack(state.getBlock(),1,state.getBlock().getMetaFromState(state)));
        List<ItemStackPython> res = items(stacks);
        world.destroyBlock(pos.pos,false);
        return res;
    }

    public void placeBlock(ItemStackPython stack,PosPython pos){
        if(!pos_available(pos))
            return;

        if(stack.stack.getItem() instanceof ItemBlock){
            ItemBlock ib= (ItemBlock) stack.stack.getItem();
            Block blk=ib.getBlock();
            ib.placeBlockAt(stack.stack,null,world,pos.pos,null,pos.getX(),pos.getY(),pos.getZ(),blk.getDefaultState());
        }
    }

    public void replaceBlock(PosPython pos, ItemStackPython stack, EnumFacing cache){
        IBlockState state=world.getBlockState(pos.pos);
        if(!state.getBlock().isAir(state,world,pos.pos)){
            List<ItemStackPython> items=breakBlock(pos);
            for(ItemStackPython item:items)
                putItems(cache, item);
        }
        placeBlock(stack,pos);
    }

    public boolean pos_available(PosPython blockPos){
        return blockPos.getY()>0;
        //return Math.abs(pos.getX()-blockPos.getX())<32 && Math.abs(pos.getY()-blockPos.getY())<32 && Math.abs(pos.getZ()-blockPos.getZ())<32;
    }
}
