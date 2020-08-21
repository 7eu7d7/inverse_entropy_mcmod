package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.items.CraftPluginItem;
import com.qtransfer.mod7e.proxy.BasePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

public class ContainerCraftPlugin extends ContainerBase implements ISyncable{
    CraftPluginItem craft;
    final int row_count=9;
    List<SlotItemHandler> slot_stuffs=new ArrayList<SlotItemHandler>();
    List<FluidSlot> slot_stuffs_fluid=new ArrayList<FluidSlot>();
    SlotFake result_item;
    FluidSlotFake result_fluid;

    public ContainerCraftPlugin(){}

    public ContainerCraftPlugin(EntityPlayer player){
        super();
        craft=new CraftPluginItem(player.getHeldItem(EnumHand.MAIN_HAND));
        init(player);
    }

    public void init(EntityPlayer player){
        int offset=0;
        int startx=9,starty=6;
        //result
        result_item=new SlotFake(craft.inventory_result, 0, 70, starty);
        result_item.setEnable(!craft.fluid);
        addSlotToContainer(result_item);

        result_fluid=new FluidSlotFake(craft.inventory_result_fluid.get(0),70, starty);
        result_fluid.setEnable(craft.fluid);
        addFluidSlot(result_fluid);
        //craft.fluid=false;

        //stuffs
        starty+=40;
        for(int i=0;i<craft.inventory.getSlots();i++) {
            SlotFake slh=new SlotFake(craft.inventory, i, startx+(offset%row_count)*18, starty+(offset/row_count)*18);
            slot_stuffs.add(slh);
            addSlotToContainer(slh);
            offset++;
        }
        //Fluid
        starty+=Math.ceil(offset/(float)row_count+1)*18+5;
        offset=0;
        for(int i=0;i<craft.inventory_fluid.size();i++) {
            FluidSlotFake slh=new FluidSlotFake(craft.inventory_fluid.get(i), startx+(offset%row_count)*18, starty+(offset/row_count)*18);
            slot_stuffs_fluid.add(slh);
            addFluidSlot(slh);
            offset++;
        }

        addPlayerSlot(player,8,150);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (playerIn.isServerWorld()) {
            craft.writeNBT();
        }
    }

    public void addItemStuff(){
        int idx=craft.addItemStuff();
        SlotFake slh=new SlotFake(craft.inventory, idx, 0, 0);
        slot_stuffs.add(slh);
        addSlotToContainer(slh);
    }

    public void addFluidStuff(){
        FluidSlotFake slh=new FluidSlotFake(craft.addFluidStuff(), 0, 0);
        slot_stuffs_fluid.add(slh);
        addFluidSlot(slh);
    }

    public void switchResult(){
        craft.fluid=!craft.fluid;
        if(craft.fluid){
            enableFluid();
        } else {
            enableItem();
        }
    }

    public void enableItem(){
        result_fluid.setEnable(false);
        result_item.setEnable(true);
        craft.fluid=false;
    }

    public void enableFluid(){
        result_fluid.setEnable(true);
        result_item.setEnable(false);
        craft.fluid=true;
    }

    @Override
    public void dataRecv(BasePacket packet) {
        switch (packet.type){
            case "item_slot_add":
                addItemStuff();
            break;
            case "fluid_slot_add":
                addFluidStuff();
                break;
            case "set_result":
                if(packet.data.equals("item"))
                    enableItem();
                else
                    enableFluid();
                break;
        }
    }
}
