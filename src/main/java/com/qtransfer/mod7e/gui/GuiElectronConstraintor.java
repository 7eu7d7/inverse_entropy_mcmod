package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.energy.CapabilityQEnergy;

public class GuiElectronConstraintor extends GUIBase{
    ContainerElectronConstraintor cec;

    public GuiElectronConstraintor(ContainerBase inventorySlotsIn) {
        super(inventorySlotsIn);
        cec= (ContainerElectronConstraintor) inventorySlotsIn;
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBG();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        int offy=30,offx=8;
        int startx=100-27,starty=6,offset=0;

        /*mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE);

        for(int i=0;i<cec.entity.inventory_energy.getSlots();i++) {
            drawScaledCustomSizeModalRect(offsetX+startx+(i%cec.row_count) * 18-2, offsetY+(i/cec.row_count) * 18+starty-2, 0, 0,80,80,16+4,16+4,80,80);
        }

        drawScaledCustomSizeModalRect(offsetX+140-2, offsetY+20-2, 0, 0,80,80,16+4,16+4,80,80);
        drawScaledCustomSizeModalRect(offsetX+140-2, offsetY+60-2, 0, 0,80,80,16+4,16+4,80,80);*/

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_PROG_MAG_BG);
        drawScaledCustomSizeModalRect(offsetX+140, offsetY+40, 0, 0,32,32,16,16,32,32);

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_PROG_MAG);
        int top=cec.entity.total_tick<=0?32:(32-32*cec.entity.energy_tick/cec.entity.total_tick);
        int[] area={0,32, 32, 32, 32, top, 0,top};
        drawTexturePoly(new Rect(offsetX+140, offsetY+40,16,16),new Rect(0,0,32,32),area,32,32);

        draw_energy(10+offsetX,50+offsetY,cec.entity.getCapability(CapabilityQEnergy.QENERGY_PROVIDER,null));

        drawSlotBG();
    }
}
