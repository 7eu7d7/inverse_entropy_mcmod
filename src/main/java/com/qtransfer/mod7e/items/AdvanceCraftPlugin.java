package com.qtransfer.mod7e.items;

import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdvanceCraftPlugin extends CraftPluginItem{
    public ArrayList<String> item_send=new ArrayList<String>();
    public ArrayList<String> fluid_send=new ArrayList<String>();

    public AdvanceCraftPlugin(ItemStack stack) {
        super(stack,false);
        deserializeNBT(stack.getTagCompound());
        if(item_send.size()==0)item_send.add("");
        if(fluid_send.size()==0)fluid_send.add("");
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = super.serializeNBT();
        nbt.setTag("item_send", strs2nbt(item_send));
        nbt.setTag("fluid_send", strs2nbt(fluid_send));
        /*nbt.setString("item_send",String.join(",", item_send));
        nbt.setString("fluid_send",String.join(",", fluid_send));*/
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt==null)
            return;
        super.deserializeNBT(nbt);
        item_send.clear();
        NBTTagList item_list=nbt.getTagList("item_send", 8);
        item_list.forEach(x -> item_send.add(((NBTTagString)x).getString()));

        fluid_send.clear();
        NBTTagList fluid_list=nbt.getTagList("fluid_send", 8);
        fluid_list.forEach(x -> fluid_send.add(((NBTTagString)x).getString()));
    }

    public NBTTagList strs2nbt(ArrayList<String> strs){
        NBTTagList nbtlist=new NBTTagList();
        strs.forEach(x -> nbtlist.appendTag(new NBTTagString(x)));
        return nbtlist;
    }

    @Override
    public int addItemStuff() {
        item_send.add("");
        return super.addItemStuff();
    }

    @Override
    public FluidTank addFluidStuff() {
        fluid_send.add("");
        return super.addFluidStuff();
    }

    public static class Register implements IItemRegister{
        @Override
        public Item registItem() {
            return new Item(){
                @Override
                public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                    if(!worldIn.isRemote) {
                        playerIn.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_ADVCRAFT_PLUGIN, worldIn, 0, 0, 0);
                    }
                    return super.onItemRightClick(worldIn, playerIn, handIn);
                }

                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                    new AdvanceCraftPlugin(stack).writeNBT();
                }

            };
        }

        @Override
        public String getRegistName() {
            return "adv_craft_plugin";
        }
    }
}
