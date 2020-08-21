package com.qtransfer.mod7e.gui;

public class GuiBlockShaper extends GUIBase{
    ContainerBlockShaper inventory;

    public GuiBlockShaper(ContainerBase inventorySlotsIn) {
        super(inventorySlotsIn);
        inventory=(ContainerBlockShaper)inventorySlotsIn;
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBG();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        int offy=30,offx=8;

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG_QBUFFER);
        drawScaledCustomSizeModalRect(offsetX+48, offsetY+5, 0, 0,256,256,80,80,256,256);

        drawSlotBG();
    }
}
