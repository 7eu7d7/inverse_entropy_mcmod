package com.qtransfer.mod7e.gui.sigchip;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.gui.*;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class GuiSetName extends GUIBase{
    GuiTextField tf_name;

    public GuiSetName(ContainerBase inventorySlotsIn) {
        super(inventorySlotsIn);
        xSize=80;
        ySize=70;
    }

    @Override
    public void initGui() {
        super.initGui();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        tf_name = new GuiTextField(10, fontRenderer, 10, 20, 60, 10);
        tf_name.setMaxStringLength(64); //设置最大长度,可省略
        tf_name.setFocused(false); //设置是否为焦点
        tf_name.setCanLoseFocus(true); //设置为可以被取消焦点
        addTextField(tf_name);

        TexButton bu_setname=new TexButton(1,0,0,40,20, I18n.format("gui.ext_plug.bu_setname"))
                .setTex(GuiElementLoader.TEXTURE_BUTTON,64,128)
                .setNormal(new Rect(0,0,64,64))
                .setHover(new Rect(0,64,64,64))
                .setPos(offsetX+20,offsetY+35);
        buttonList.add(bu_setname);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id)
        {
            case 1:
                //String text=tf_name.getText();
                //QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","set_name",text));
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
        fontRenderer.drawStringWithShadow(tf_name.getText(), offsetX+20, offsetY+5, 16777215);
    }
}
