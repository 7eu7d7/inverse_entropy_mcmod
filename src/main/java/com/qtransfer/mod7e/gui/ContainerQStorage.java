package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.blocks.QuantumChestEntity;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import com.qtransfer.mod7e.transfer.GeneralStack;
import com.qtransfer.mod7e.transfer.IStorageable;
import com.qtransfer.mod7e.utils.IHandlerSolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ContainerQStorage extends ContainerBase implements ISyncable{
    IStorageable storage;
    public volatile ArrayList<GeneralStack> st_list=new ArrayList<GeneralStack>();
    EntityPlayer player;
    GuiQStorage guist=null;

    public ContainerQStorage(EntityPlayer player, IStorageable storage){
        super();
        this.player=player;
        int offy=30,offx=8;
        this.storage=storage;

        int startx=100-27,starty=6,offset=0;

        addPlayerSlot(player,offx,150);


        if(player.world.isRemote) {
            //向服务器请求物品数据
            QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","req_storage_list",""));
        }
    }

    public void init_storage() {

        NBTTagCompound nbt=new NBTTagCompound();
        NBTTagList nbtlist=new NBTTagList();

        //添加储存中有的
        for(Map.Entry<GeneralStack,Integer> entry:storage.getStorgeMap().entrySet()){
            GeneralStack stack=entry.getKey().copy();
            stack.setCount(entry.getValue());
            nbtlist.appendTag(stack.serializeNBT());
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
                st_list.add(new GeneralStack(nbtlist.getCompoundTagAt(i)));
            }
            //System.out.println("set_st:"+st_list.size()+","+guist+","+this);
            if(guist!=null)
                guist.updateStorage();
        } catch (NBTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if(!player.world.isRemote)
            storage.saveNBT();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if(storage instanceof QuantumChestEntity && ((QuantumChestEntity) storage).change_mark) {
            init_storage();
            ((QuantumChestEntity) storage).change_mark=false;
        }
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
            case "add_fluid": { //server only
                try {
                    NBTTagCompound nbt=JsonToNBT.getTagFromJson(packet.data);
                    GeneralStack stack=new GeneralStack(nbt);
                    IFluidHandlerItem handler=stack.stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null);
                    FluidStack f_add=handler.drain(Integer.MAX_VALUE,true);
                    handler.fill(storage.addItem(new GeneralStack(f_add),true).fstack,true);
                    init_storage();
                    stack=new GeneralStack(handler.getContainer());
                    player.inventory.setItemStack(stack.stack);
                    //同步至客户端
                    QNetworkManager.INSTANCE.sendPacketToPlayer(new BasePacket("container","rest_items",stack.serializeNBT().toString()) , (EntityPlayerMP) player);
                } catch (NBTException e) {
                    e.printStackTrace();
                }
            }break;
            case "add_storage": { //server only
                try {
                    NBTTagCompound nbt=JsonToNBT.getTagFromJson(packet.data);
                    GeneralStack rest=storage.addItem(new GeneralStack(nbt),true);
                    init_storage();
                    player.inventory.setItemStack(rest.stack);
                    //同步至客户端
                    QNetworkManager.INSTANCE.sendPacketToPlayer(new BasePacket("container","rest_items",rest.serializeNBT().toString()) , (EntityPlayerMP) player);
                } catch (NBTException e) {
                    e.printStackTrace();
                }
            }break;
            case "take_storage": { //server only
                try {
                    NBTTagCompound nbt=JsonToNBT.getTagFromJson(packet.data);
                    GeneralStack st_take=new GeneralStack(nbt);
                    st_take=storage.takeItem(st_take,st_take.getCount(),true);
                    //init_storage();
                    ItemStack held=player.inventory.getItemStack();
                    if(st_take.fluid && held.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null)){
                        IFluidHandlerItem handler=held.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null);
                        FluidStack rest=Utils.fluidFill(handler,st_take.fstack);
                        if(rest!=null && rest.amount>0)
                            storage.addItem(new GeneralStack(rest),true);
                        st_take=new GeneralStack(handler.getContainer());
                    }
                    player.inventory.setItemStack(st_take.stack);
                    init_storage();
                    //同步至客户端
                    QNetworkManager.INSTANCE.sendPacketToPlayer(new BasePacket("container","rest_items",st_take.serializeNBT().toString()) , (EntityPlayerMP) player);
                } catch (NBTException e) {
                    e.printStackTrace();
                }
            }break;
            case "rest_items":{ //client only
                try {
                    //System.out.println(packet.data);
                    NBTTagCompound nbt=JsonToNBT.getTagFromJson(packet.data);
                    player.inventory.setItemStack(new GeneralStack(nbt).stack);
                } catch (NBTException e) {
                    e.printStackTrace();
                }
            }break;
        }
    }
}
