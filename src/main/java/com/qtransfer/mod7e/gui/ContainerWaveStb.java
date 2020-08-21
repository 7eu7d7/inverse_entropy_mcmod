package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.blocks.QTransBlock;
import com.qtransfer.mod7e.blocks.transfer.WaveStabilizerEntity;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import com.qtransfer.mod7e.transfer.GeneralStack;
import com.qtransfer.mod7e.utils.IHandlerSolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ContainerWaveStb extends ContainerBase implements ISyncable{
    public WaveStabilizerEntity wse;
    ArrayList<ItemStackGui> st_list=new ArrayList<ItemStackGui>();
    EntityPlayer player;

    public ContainerWaveStb(EntityPlayer player, TileEntity tile){
        super();
        this.player=player;
        wse=(WaveStabilizerEntity)tile;

        addPlayerSlot(player,10,140);

        for(int i=0;i<wse.inventory.getSlots();i++) {
            addSlotToContainer(new SlotItemHandler(wse.inventory, i, 10+(i%9) * 18, (i/9) * 18+70));
        }

        if(!player.world.isRemote) {
            QTransBlock.updateQuantumNet(wse.getWorld(),wse.getPos()); //开启GUI时更新网络
        } else {
            //向服务器请求物品数据
            QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","req_storage_list",""));
        }

        for(int i=0;i<wse.fluid_inventory.size();i++){
            addFluidSlot(new FluidSlot(wse.fluid_inventory.get(i),10+18*(i%9),50+18*(i/9)));
        }
    }

    public void init_storage() {
        NBTTagCompound nbt=new NBTTagCompound();
        NBTTagList nbtlist=new NBTTagList();

        //添加储存中有的
        for(Map.Entry<GeneralStack,Collection<IHandlerSolt>> entry:wse.storage_map.asMap().entrySet()){
            GeneralStack stack=entry.getKey().copy();
            int count=0;
            for(IHandlerSolt i:entry.getValue())
                count+=i.getCount();
            nbtlist.appendTag(new ItemStackGui(stack,count).serializeNBT());
            //st_list.add(new GeneralStack(stack));
        }

        //添加可合成的
        for(GeneralStack ish:wse.req_map.keySet()){
            if(!wse.storage_map.containsKey(ish)){
                GeneralStack stack=ish.copy();
                nbtlist.appendTag(new ItemStackGui(stack,0).serializeNBT());
            }
        }


        nbt.setTag("storage", nbtlist);

        //同步至客户端
        QNetworkManager.INSTANCE.sendPacketToPlayer(new BasePacket("container","storage_list",nbt.toString()) , (EntityPlayerMP) player);
    }

    public void setStorageList(String json){
        try {
            NBTTagCompound nbt=JsonToNBT.getTagFromJson(json);
            NBTTagList nbtlist=nbt.getTagList("storage",nbt.getId());
            st_list.clear();
            for(int i=0;i<nbtlist.tagCount();i++){
                st_list.add(new ItemStackGui(nbtlist.getCompoundTagAt(i)));
            }
        } catch (NBTException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        return null;
    }

    @Override
    public void dataRecv(BasePacket packet) {
        switch (packet.type){
            case "storage_list":
                setStorageList(packet.data);
                break;
            case "req_storage_list":
                init_storage();
                break;
            case "req_items": {
                try {
                    NBTTagCompound nbt=JsonToNBT.getTagFromJson(packet.data);
                    wse.generateTree(GeneralStack.unpackNBT(nbt));
                    wse.checkStuff();
                    wse.stratCraft();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }break;
        }
    }

    public class ItemStackGui implements INBTSerializable<NBTTagCompound> {
        GeneralStack stack;
        int count;

        public ItemStackGui(GeneralStack stack, int count){
            this.stack=stack;
            this.count=count;
        }

        public ItemStackGui(NBTTagCompound nbt){
            deserializeNBT(nbt);
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbt=new NBTTagCompound();
            nbt.setTag("stack",stack.serializeNBT());
            nbt.setInteger("count",count);
            return nbt;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            stack=new GeneralStack(nbt.getCompoundTag("stack"));
            count=nbt.getInteger("count");
        }

        public ItemStackGui setCount(int count){
            this.count=count;
            return this;
        }
    }
}
