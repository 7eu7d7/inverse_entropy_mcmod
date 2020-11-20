package com.qtransfer.mod7e.items;

import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.gui.GuiElementLoader;
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

import javax.annotation.Nullable;
import java.util.List;

public class StoragePluginItem implements INBTSerializable<NBTTagCompound> {
    public EnumFacing face=EnumFacing.DOWN;
    ItemStack storage;

    public StoragePluginItem(ItemStack stack){
        storage=stack;
        deserializeNBT(stack.getTagCompound());
    }

    public void writeNBT(){
        storage.setTagCompound(serializeNBT());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        nbt.setInteger("face",face.getIndex());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt==null)
            return;
        face=EnumFacing.VALUES[nbt.getInteger("face")];
    }

    public void setFace(EnumFacing face){
        this.face=face;
    }

    public static class Register implements IItemRegister{
        @Override
        public Item registItem() {
            return new Item(){
                @Override
                public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                    if(!worldIn.isRemote) {
                        playerIn.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_STORAGE_PLUGIN, worldIn, 0, 0, 0);
                    }
                    return super.onItemRightClick(worldIn, playerIn, handIn);
                }

                @Override
                public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                    new StoragePluginItem(stack).writeNBT();
                }
            };
        }

        @Override
        public String getRegistName() {
            return "storage_plugin";
        }
    }
}
