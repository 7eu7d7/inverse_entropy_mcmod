package com.qtransfer.mod7e;

import com.qtransfer.mod7e.energy.CapabilityQEnergy;
import com.qtransfer.mod7e.gui.GuiElementLoader;
import com.qtransfer.mod7e.items.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Vector;

public class ItemMaker {
    public static Vector<Item> itemlist=new Vector<Item>();

    public ItemMaker(){
        addStuffs();
    }

    public void addStuffs(){
        String[] stuffs=Utils.readAssets("stuff_list.txt").split("\n");
        for(String i:stuffs) {
            int start=0;
            if((start=i.indexOf("["))!=-1){
                String ore=i.substring(start+1,i.indexOf("]",start));
                i=i.substring(0,start);
                addItem(new Item(), i.trim());
                OreDictionary.registerOre(ore,itemlist.lastElement());
            }else
                addItem(new Item(), i.trim());
        }

        String[] ores=Utils.readAssets("oredict.txt").split("\n");
        for(String i:ores) {
            i=i.trim();
            int start=0;
            if((start=i.indexOf("["))!=-1){
                String ore=i.substring(start+1,i.indexOf("]",start));
                i=i.substring(0,start);
                OreDictionary.registerOre(ore,Item.getByNameOrId(i));
            }
        }

        addItem(new Item(){
            @Override
            public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                playerIn.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_CRAFT_PLUGIN, worldIn, 0, 0, 0);
                return super.onItemRightClick(worldIn, playerIn, handIn);
            }

            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                new CraftPluginItem(stack).writeNBT();
            }

        },"craft_plugin");

        addItem(new Item(){
            @Override
            public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                playerIn.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_ADVCRAFT_PLUGIN, worldIn, 0, 0, 0);
                return super.onItemRightClick(worldIn, playerIn, handIn);
            }

            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                new AdvanceCraftPlugin(stack).writeNBT();
            }

        },"adv_craft_plugin");

        addItem(new Item(){
            @Override
            public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                playerIn.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_STORAGE_PLUGIN, worldIn, 0, 0, 0);
                return super.onItemRightClick(worldIn, playerIn, handIn);
            }

            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                new StoragePluginItem(stack).writeNBT();
            }
        },"storage_plugin");

        addItem(new Item(){
            @Override
            public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                playerIn.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_EXTRACT_PLUGIN, worldIn, 0, 0, 0);
                return super.onItemRightClick(worldIn, playerIn, handIn);
            }

            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                new ExtractPluginItem(stack).writeNBT();
            }
        },"extract_plugin");

        addItem(new Item(){
            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                new StorageItem(stack).saveNBT();
            }
        },"quantum_ball");

        addItem(new Item(){

            @SideOnly(Side.CLIENT)
            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                QuantumSuperpositionBall qsb=new QuantumSuperpositionBall(stack);
                if(!stack.hasTagCompound())
                    qsb.writeNBT();
                tooltip.add(Utils.getShowNum(qsb.energy.getEnergy())+"/"+Utils.getShowNum(qsb.energy.getCapacity()));
                NBTTagCompound nbt=stack.getTagCompound();
                if(nbt.getBoolean("outfe"))
                    tooltip.add("energy convert upgrade");
            }

            @Override
            public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
                return new ICapabilityProvider() {
                    @Override
                    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
                        NBTTagCompound nbt=stack.getTagCompound();
                        return cap == CapabilityQEnergy.QENERGY_USER || (cap == CapabilityEnergy.ENERGY && nbt!=null && nbt.getBoolean("outfe"));
                    }
                    @Override
                    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
                        NBTTagCompound nbt=stack.getTagCompound();
                        if (cap == CapabilityQEnergy.QENERGY_USER) {
                            return CapabilityQEnergy.QENERGY_USER.cast(new QuantumSuperpositionBall(stack).energy);
                        } else if(cap == CapabilityEnergy.ENERGY && nbt!=null && nbt.getBoolean("outfe")){
                            return CapabilityEnergy.ENERGY.cast(new QuantumSuperpositionBall(stack).fe_energy);
                        } else{
                            return null;
                        }
                    }
                };
            }
        },"quantum_superposition_ball");

        addItem(new Item(){
            @Override
            public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                playerIn.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_SINGLE_CHIP, worldIn, 0, 0, 0);
                return super.onItemRightClick(worldIn, playerIn, handIn);
            }

            @Override
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                //new SingleChipItem(stack).writeNBT();
            }
        },"single_chip");

        addItem(new Item(){
            @Override
            public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
                playerIn.sendMessage(new TextComponentString(String.format("location:{x:%d y:%d z:%d}", (int)playerIn.posX, (int)playerIn.posY, (int)playerIn.posZ)));
                return super.onItemRightClick(worldIn, playerIn, handIn);
            }
        },"location_getter");
    }

    public static void addItem(Item item,String reg_name){
        item.setRegistryName(reg_name).setUnlocalizedName(reg_name).setCreativeTab(TabsList.Quantum_Transfer_TAB);
        itemlist.add(item);
    }

}
