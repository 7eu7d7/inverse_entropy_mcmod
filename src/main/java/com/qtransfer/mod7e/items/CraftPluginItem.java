package com.qtransfer.mod7e.items;

import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import com.qtransfer.mod7e.transfer.GeneralStack;
import com.qtransfer.mod7e.utils.CraftItemHandler;
import com.qtransfer.mod7e.utils.FluidTankList;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CraftPluginItem implements INBTSerializable<NBTTagCompound> {
    public CraftItemHandler inventory=new CraftItemHandler();
    public FluidTankList inventory_fluid=new FluidTankList(1);

    public CraftItemHandler inventory_result=new CraftItemHandler();
    public FluidTankList inventory_result_fluid=new FluidTankList(1);
    public boolean fluid=false;

    public EnumFacing face=EnumFacing.DOWN;
    ItemStack craft;

    public CraftPluginItem(ItemStack stack){
        this(stack,true);
    }

    public CraftPluginItem(ItemStack stack,boolean init){
        craft=stack;
        if (init)
            deserializeNBT(stack.getTagCompound());
    }

    public void writeNBT(){
        craft.setTagCompound(serializeNBT());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        nbt.setTag("inv_stuffs",inventory.serializeNBT());
        nbt.setTag("inv_fluid",inventory_fluid.serializeNBT());
        nbt.setTag("inv_result",inventory_result.serializeNBT());
        nbt.setTag("inv_result_fluid",inventory_result_fluid.serializeNBT());
        nbt.setInteger("face",face.getIndex());
        nbt.setBoolean("fluid_on",fluid);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt==null)
            return;
        inventory.deserializeNBT(nbt.getCompoundTag("inv_stuffs"));
        inventory_fluid.deserializeNBT(nbt.getCompoundTag("inv_fluid"));
        inventory_result.deserializeNBT(nbt.getCompoundTag("inv_result"));
        inventory_result_fluid.deserializeNBT(nbt.getCompoundTag("inv_result_fluid"));
        face=EnumFacing.VALUES[nbt.getInteger("face")];
        fluid=nbt.getBoolean("fluid_on");
    }

    public void setFace(EnumFacing face){
        this.face=face;
    }

    public int addItemStuff(){
        return inventory.addStuffSolt();
    }

    public FluidTank addFluidStuff(){
        return inventory_fluid.addTank();
    }

    public GeneralStack getResult(){
        if(fluid)
            System.out.println(inventory_result_fluid.get(0).getFluid());
        return fluid?new GeneralStack(inventory_result_fluid.get(0).getFluid()):new GeneralStack(inventory_result.getStackInSlot(0));
    }

    public List<GeneralStack> getStuffs(){
        HashMap<GeneralStack, Integer> stuffs=new HashMap<GeneralStack, Integer>();
        //List<GeneralStack> stuffs=new ArrayList<GeneralStack>();
        //添加物品合成
        for(int i=0;i<inventory.getSlots();i++){
            if(!inventory.getStackInSlot(i).isEmpty()) {
                GeneralStack ish=new GeneralStack(inventory.getStackInSlot(i)).copy();
                if(!stuffs.containsKey(ish))
                    stuffs.put(ish,0);
                stuffs.put(ish, stuffs.get(ish)+ish.getCount());
            }
        }

        //添加流体合成
        for(int i=0;i<inventory_fluid.size();i++){
            if(inventory_fluid.get(i).getFluidAmount()>0) {
                GeneralStack ish=new GeneralStack(inventory_fluid.get(i).getFluid()).copy();
                if(!stuffs.containsKey(ish))
                    stuffs.put(ish,0);
                stuffs.put(ish, stuffs.get(ish)+ish.getCount());
            }
        }

        List<GeneralStack> stuffs_list=new ArrayList<GeneralStack>(stuffs.keySet());
        for(GeneralStack ish:stuffs_list)
            ish.setCount(stuffs.get(ish));
        return stuffs_list;
    }

    public List<GeneralStack> getStuffs_raw(){
        List<GeneralStack> stuffs=new ArrayList<GeneralStack>();
        for(int i=0;i<inventory.getSlots();i++){
            stuffs.add(new GeneralStack(inventory.getStackInSlot(i)).copy());
        }
        for(int i=0;i<inventory_fluid.size();i++){
            GeneralStack stack=new GeneralStack(inventory_fluid.get(i).getFluid());
            stuffs.add(stack.isEmpty()?stack.getEmpty():stack.copy());
        }
        return stuffs;
    }

    public static class Register implements IItemRegister{
        @Override
        public Item registItem() {
            return new Item(){
                @Override
                public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                    if(!worldIn.isRemote) {
                        playerIn.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_CRAFT_PLUGIN, worldIn, 0, 0, 0);
                    }
                    return super.onItemRightClick(worldIn, playerIn, handIn);
                }

                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                    new CraftPluginItem(stack).writeNBT();
                }

            };
        }

        @Override
        public String getRegistName() {
            return "craft_plugin";
        }
    }
}
