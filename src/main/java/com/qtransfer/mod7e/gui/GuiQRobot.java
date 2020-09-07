package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class GuiQRobot extends GUIBase{
    ContainerQRobot inventory;
    TexButton bu_run,bu_stop;

    public GuiQRobot(ContainerBase inventorySlotsIn) {
        super(inventorySlotsIn);
        this.xSize=210;
        inventory=(ContainerQRobot)inventorySlotsIn;
    }

    @Override
    public void initGui() {
        super.initGui();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        bu_run=new TexButton(1,0,0,16,16,"")
                .setTex(GuiElementLoader.TEXTURE_BU_START,32,64)
                .setNormal(new Rect(0,0,32,32))
                .setHover(new Rect(0,32,32,32))
                .setPos(2+offsetX,60+offsetY);
        buttonList.add(bu_run);

        bu_stop=new TexButton(2,0,0,16,16,"")
                .setTex(GuiElementLoader.TEXTURE_BU_STOP,32,64)
                .setNormal(new Rect(0,0,32,32))
                .setHover(new Rect(0,32,32,32))
                .setPos(22+offsetX,60+offsetY);
        buttonList.add(bu_stop);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id)
        {
            case 1:
                QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","qrobot_start",""));
                break;
            case 2:
                QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","qrobot_stop",""));
                break;
            default:
                super.actionPerformed(button);
                return;
        }
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBG();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_CHIP_BG);
        drawScaledCustomSizeModalRect(offsetX+20-20, offsetY+45-20, 0, 0,256,256,32+8,32+8,256,256);

        drawSlotBG();
    }
}
