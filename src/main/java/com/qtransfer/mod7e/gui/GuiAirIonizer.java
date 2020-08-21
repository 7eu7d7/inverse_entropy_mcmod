package com.qtransfer.mod7e.gui;

public class GuiAirIonizer extends GUIBase{
    public GuiAirIonizer(ContainerBase inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBG();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        int offy=30,offx=8;

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG_QBUFFER);
        drawScaledCustomSizeModalRect(offsetX+48, offsetY+5, 0, 0,256,256,80,80,256,256);

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE);
        drawScaledCustomSizeModalRect(80+offsetX-2, 45-8+offsetY-2, 0, 0,80,80,16+4,16+4,80,80);

        //绘制玩家物品
        drawPlayerInv(offsetX-2+offx,offsetY-2+97);
    }
}
