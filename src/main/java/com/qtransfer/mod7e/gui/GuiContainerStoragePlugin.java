package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.io.IOException;

public class GuiContainerStoragePlugin extends GUIBase{
    TexButton[] bulist=new TexButton[6];
    ContainerStoragePlugin csp;
    ItemStack stack_center;

    public GuiContainerStoragePlugin(ContainerBase inventorySlotsIn) {
        super(inventorySlotsIn);
        csp=(ContainerStoragePlugin)inventorySlotsIn;
        stack_center=new ItemStack(Item.getByNameOrId("minecraft:stone"));
    }

    @Override
    public void initGui() {
        super.initGui();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        bulist[EnumFacing.UP.getIndex()]=new TexButton(EnumFacing.UP.getIndex(),0,0,20,20,"U")
                .setTex(GuiElementLoader.TEXTURE_BUTTON,64,128)
                .setNormal(new Rect(0,0,64,64))
                .setHover(new Rect(0,64,64,64))
                .setPos(offsetX+80,offsetY+50);
        buttonList.add(bulist[EnumFacing.UP.getIndex()]);

        bulist[EnumFacing.DOWN.getIndex()]=new TexButton(EnumFacing.DOWN.getIndex(),0,0,20,20,"D")
                .setTex(GuiElementLoader.TEXTURE_BUTTON,64,128)
                .setNormal(new Rect(0,0,64,64))
                .setHover(new Rect(0,64,64,64))
                .setPos(offsetX+80,offsetY+110);
        buttonList.add(bulist[EnumFacing.DOWN.getIndex()]);

        bulist[EnumFacing.WEST.getIndex()]=new TexButton(EnumFacing.WEST.getIndex(),0,0,20,20,"W")
                .setTex(GuiElementLoader.TEXTURE_BUTTON,64,128)
                .setNormal(new Rect(0,0,64,64))
                .setHover(new Rect(0,64,64,64))
                .setPos(offsetX+50,offsetY+80);
        buttonList.add(bulist[EnumFacing.WEST.getIndex()]);

        bulist[EnumFacing.EAST.getIndex()]=new TexButton(EnumFacing.EAST.getIndex(),0,0,20,20,"E")
                .setTex(GuiElementLoader.TEXTURE_BUTTON,64,128)
                .setNormal(new Rect(0,0,64,64))
                .setHover(new Rect(0,64,64,64))
                .setPos(offsetX+110,offsetY+80);
        buttonList.add(bulist[EnumFacing.EAST.getIndex()]);

        bulist[EnumFacing.NORTH.getIndex()]=new TexButton(EnumFacing.NORTH.getIndex(),0,0,20,20,"N")
                .setTex(GuiElementLoader.TEXTURE_BUTTON,64,128)
                .setNormal(new Rect(0,0,64,64))
                .setHover(new Rect(0,64,64,64))
                .setPos(offsetX+50,offsetY+110);
        buttonList.add(bulist[EnumFacing.NORTH.getIndex()]);

        bulist[EnumFacing.SOUTH.getIndex()]=new TexButton(EnumFacing.SOUTH.getIndex(),0,0,20,20,"S")
                .setTex(GuiElementLoader.TEXTURE_BUTTON,64,128)
                .setNormal(new Rect(0,0,64,64))
                .setHover(new Rect(0,64,64,64))
                .setPos(offsetX+110,offsetY+50);
        buttonList.add(bulist[EnumFacing.SOUTH.getIndex()]);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        csp.spi.setFace(EnumFacing.VALUES[button.id]);
        QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","face",csp.spi.face.getName().toLowerCase()));
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        //GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG);
        drawScaledCustomSizeModalRect(offsetX, offsetY, 0, 0,979,695,xSize,ySize,979,695);

        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();

        drawItemStack(stack_center,offsetX+80+2,offsetY+80+2,"");

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        drawCenteredString(fontRenderer,csp.spi.face.getName(),offsetX+20,offsetY+30,0xffffff);
    }
}
