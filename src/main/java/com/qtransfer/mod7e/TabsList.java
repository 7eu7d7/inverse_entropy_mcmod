package com.qtransfer.mod7e;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TabsList {
    public static CreativeTabs Quantum_Transfer_TAB;

    @SideOnly(Side.CLIENT)
    public static void addCreativeTab()
    {
        Quantum_Transfer_TAB = new CreativeTabs("QuantumTransfer") {
            @Override
            public ItemStack getTabIconItem() {
            return new ItemStack(Item.getByNameOrId("qtrans:air_ionizer"));
        }
        };
    }


}
