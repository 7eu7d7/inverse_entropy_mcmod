package com.qtransfer.mod7e.items;

import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import com.qtransfer.mod7e.utils.CraftItemHandler;
import com.qtransfer.mod7e.utils.FluidTankList;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ExtractPluginItem implements INBTSerializable<NBTTagCompound> {

    ItemStack stack;
    public CraftItemHandler inventory_item=new CraftItemHandler(9);
    public FluidTankList inventory_fluid=new FluidTankList(9);
    public String target="";

    public ExtractPluginItem(ItemStack stack){
        this.stack=stack;
        deserializeNBT(stack.getTagCompound());
    }

    public void writeNBT(){
        stack.setTagCompound(serializeNBT());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        nbt.setTag("inventory_item",inventory_item.serializeNBT());
        nbt.setTag("inventory_fluid",inventory_fluid.serializeNBT());
        nbt.setString("target",target);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt==null)
            return;
        inventory_item.deserializeNBT(nbt.getCompoundTag("inventory_item"));
        inventory_fluid.deserializeNBT(nbt.getCompoundTag("inventory_fluid"));
        target=nbt.getString("target");
    }

    public static class Register implements IItemRegister{
        @Override
        public Item registItem() {
            return new Item(){
                @Override
                public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                    if(!worldIn.isRemote) {
                        playerIn.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_EXTRACT_PLUGIN, worldIn, 0, 0, 0);
                    }
                    return super.onItemRightClick(worldIn, playerIn, handIn);
                }

                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                    new ExtractPluginItem(stack).writeNBT();
                }
            };
        }

        @Override
        public String getRegistName() {
            return "extract_plugin";
        }
    }
}
