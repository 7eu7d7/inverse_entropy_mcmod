package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Constant;
import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.gui.sigchip.GuiSingleChipList;
import com.qtransfer.mod7e.items.QuantumBagItem;
import com.qtransfer.mod7e.transfer.IStorageable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiElementLoader implements IGuiHandler
{
    public static final int GUI_H2O_DECOMP = 1;
    public static final int GUI_WAVE_STB = 2;
    public static final int GUI_Q_INTER = 3;
    public static final int GUI_Q_BUFFER = 4;
    public static final int GUI_BLOCK_SHAPER = 5;
    public static final int GUI_Q_CHEST = 6;
    public static final int GUI_QSTORAGE = 7;

    public static final int GUI_CRAFT_PLUGIN = 10;
    public static final int GUI_STORAGE_PLUGIN = 11;
    public static final int GUI_EXTRACT_PLUGIN = 12;
    public static final int GUI_SINGLE_CHIP = 13;
    public static final int GUI_ADVCRAFT_PLUGIN = 14;
    public static final int GUI_QSTORAGE_ITEM = 15;
    public static final int GUI_Q_BAG = 16;

    public static final int GUI_AIR_IONIZER = 20;
    public static final int GUI_ELECTRON_CONSTRAINTOR = 21;
    public static final int GUI_ENERGY_BOX = 22;

    public static final int GUI_Q_ROBOT = 101;


    public static final ResourceLocation TEXTURE = new ResourceLocation(Constant.item("textures/gui/slot_bg.png"));
    public static final ResourceLocation TEXTURE_BG = new ResourceLocation(Constant.item("textures/gui/bg1.png"));
    public static final ResourceLocation TEXTURE_BG_FLU = new ResourceLocation(Constant.item("textures/gui/bg_flu.png"));
    public static final ResourceLocation TEXTURE_SUN= new ResourceLocation(Constant.item("textures/gui/sun.png"));
    public static final ResourceLocation TEXTURE_SUN_BG= new ResourceLocation(Constant.item("textures/gui/sun_bg.png"));
    public static final ResourceLocation TEXTURE_ARROW= new ResourceLocation(Constant.item("textures/gui/arrow.png"));
    public static final ResourceLocation TEXTURE_SLOT_CIRCLE= new ResourceLocation(Constant.item("textures/gui/slot_circle.png"));
    public static final ResourceLocation TEXTURE_BG_SELECT= new ResourceLocation(Constant.item("textures/gui/bg_select.png"));
    public static final ResourceLocation TEXTURE_BG_WSTB= new ResourceLocation(Constant.item("textures/gui/wstb_bg.png"));
    public static final ResourceLocation TEXTURE_BG_QBUFFER= new ResourceLocation(Constant.item("textures/gui/qbuffer_bg.png"));
    public static final ResourceLocation TEXTURE_ENERGY_BAR= new ResourceLocation(Constant.item("textures/gui/energy_bar.png"));

    public static final ResourceLocation TEXTURE_PROG_MAG= new ResourceLocation(Constant.item("textures/gui/prog_magnet.png"));
    public static final ResourceLocation TEXTURE_PROG_MAG_BG= new ResourceLocation(Constant.item("textures/gui/prog_magnet_bg.png"));
    public static final ResourceLocation TEXTURE_FLUID_IO= new ResourceLocation(Constant.item("textures/gui/fluid_io.png"));

    public static final ResourceLocation TEXTURE_BU_ADD= new ResourceLocation(Constant.item("textures/gui/bu_add.png"));
    public static final ResourceLocation TEXTURE_BUTTON= new ResourceLocation(Constant.item("textures/gui/button.png"));
    public static final ResourceLocation TEXTURE_BU_ADD_SEND= new ResourceLocation(Constant.item("textures/gui/bu_add_send.png"));
    public static final ResourceLocation TEXTURE_BU_START= new ResourceLocation(Constant.item("textures/gui/bu_start.png"));
    public static final ResourceLocation TEXTURE_BU_STOP= new ResourceLocation(Constant.item("textures/gui/bu_stop.png"));

    public static final ResourceLocation TEXTURE_CHIP_BG= new ResourceLocation(Constant.item("textures/gui/chip_bg.png"));

    public static final ResourceLocation TEXTURE_ICON_PY= new ResourceLocation(Constant.item("textures/gui/icon_python.png"));
    public static final ResourceLocation TEXTURE_ICON_TXT= new ResourceLocation(Constant.item("textures/gui/icon_txt.png"));
    public static final ResourceLocation TEXTURE_ICON_IMG= new ResourceLocation(Constant.item("textures/gui/icon_img.png"));
    public static final ResourceLocation TEXTURE_ICON_NONE= new ResourceLocation(Constant.item("textures/gui/icon_none.png"));

    public GuiElementLoader()
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(QuantumTransfer.instance, this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID) {
            case GUI_H2O_DECOMP:
                return new ContainerH2ODe(player, world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_WAVE_STB:
                return new ContainerWaveStb(player, world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_Q_INTER:
                return new ContainerQInterface(player, world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_Q_BUFFER:
                return new ContainerQBuffer(player, world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_Q_CHEST:
                return new ContainerQuantumChest(player, world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_QSTORAGE:
                return new ContainerQStorage(player, (IStorageable) world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_BLOCK_SHAPER:
                return new ContainerBlockShaper(player, world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_CRAFT_PLUGIN:
                return new ContainerCraftPlugin(player);
            case GUI_ADVCRAFT_PLUGIN:
                return new ContainerAdvanceCP(player);
            case GUI_STORAGE_PLUGIN:
                return new ContainerStoragePlugin(player);
            case GUI_EXTRACT_PLUGIN:
                return new ContainerExtractPlugin(player);
            case GUI_SINGLE_CHIP:
                return new ContainerSingleChip(player);
            case GUI_QSTORAGE_ITEM:
                return new ContainerQStorage(player, new QuantumBagItem(player.getHeldItem(EnumHand.MAIN_HAND)));
            case GUI_Q_BAG:
                return new ContainerQuantumBag(player);
            case GUI_AIR_IONIZER:
                return new ContainerAirIonizer(player, world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_ELECTRON_CONSTRAINTOR:
                return new ContainerElectronConstraintor(player, world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_ENERGY_BOX:
                return new ContainerEnergyBox(player, world.getTileEntity(new BlockPos(x, y, z)));

            case GUI_Q_ROBOT:
                return new ContainerQRobot(player, world.getEntityByID(x));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID) {
            case GUI_H2O_DECOMP:
                return new GuiContainerH2ODe(new ContainerH2ODe(player, world.getTileEntity(new BlockPos(x, y, z))));
            case GUI_WAVE_STB:
                return new GuiContainerWaveStb(new ContainerWaveStb(player, world.getTileEntity(new BlockPos(x, y, z))));
            case GUI_Q_INTER:
                return new GuiContainerQInterface(new ContainerQInterface(player, world.getTileEntity(new BlockPos(x, y, z))));
            case GUI_Q_BUFFER:
                return new GuiContainerQBuffer(new ContainerQBuffer(player, world.getTileEntity(new BlockPos(x, y, z))));
            case GUI_Q_CHEST:
                return new GUIBase(new ContainerQuantumChest(player, world.getTileEntity(new BlockPos(x, y, z))));
            case GUI_QSTORAGE:
                return new GuiQStorage(new ContainerQStorage(player, (IStorageable) world.getTileEntity(new BlockPos(x, y, z))));
            case GUI_BLOCK_SHAPER:
                return new GuiBlockShaper(new ContainerBlockShaper(player, world.getTileEntity(new BlockPos(x, y, z))));
            case GUI_CRAFT_PLUGIN:
                return new GuiContainerCraftPlugin(new ContainerCraftPlugin(player));
            case GUI_ADVCRAFT_PLUGIN:
                return new GuiAdvanceCP(new ContainerAdvanceCP(player));
            case GUI_STORAGE_PLUGIN:
                return new GuiContainerStoragePlugin(new ContainerStoragePlugin(player));
            case GUI_EXTRACT_PLUGIN:
                return new GuiExtractPlugin(new ContainerExtractPlugin(player));
            case GUI_SINGLE_CHIP:
                return new GuiSingleChipList(new ContainerSingleChip(player));
            case GUI_QSTORAGE_ITEM:
                return new GuiQStorage(new ContainerQStorage(player, new QuantumBagItem(player.getHeldItem(EnumHand.MAIN_HAND))));
            case GUI_Q_BAG:
                return new GUIBase(new ContainerQuantumBag(player));
            case GUI_AIR_IONIZER:
                return new GuiAirIonizer(new ContainerAirIonizer(player,world.getTileEntity(new BlockPos(x, y, z))));
            case GUI_ELECTRON_CONSTRAINTOR:
                return new GuiElectronConstraintor(new ContainerElectronConstraintor(player,world.getTileEntity(new BlockPos(x, y, z))));
            case GUI_ENERGY_BOX:
                return new GUIBase(new ContainerEnergyBox(player, world.getTileEntity(new BlockPos(x, y, z))));

            case GUI_Q_ROBOT:
                return new GuiQRobot(new ContainerQRobot(player, world.getEntityByID(x)));
        }
        return null;
    }

    public static ResourceLocation getIcon(String file){
        switch (file.substring(file.lastIndexOf(".")+1)){
            case "py":
                return TEXTURE_ICON_PY;
            case "txt":
                return TEXTURE_ICON_TXT;
            case "jpg":
            case "bmp":
            case "png":
                return TEXTURE_ICON_IMG;
            default:
                return TEXTURE_ICON_NONE;
        }
    }

}
