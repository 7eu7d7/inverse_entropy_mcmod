package com.qtransfer.mod7e.gui.sigchip;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.gui.*;
import com.qtransfer.mod7e.gui.widget.ListView;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class GuiSingleChipList extends GUIBase {
    //GuiMultiLineTextField tf_code;
    public ListView code_list;
    ContainerSingleChip csc;
    TexButton bu_add;

    public GuiSingleChipList(ContainerBase inventorySlotsIn) {
        super(inventorySlotsIn);
        this.xSize = 220;
        csc=(ContainerSingleChip)inventorySlotsIn;
        csc.gui_chip_list=this;
    }

    @Override
    public void initGui() {
        super.initGui();

        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

        bu_add=new TexButton(1,0,0,16,16,"")
                .setTex(GuiElementLoader.TEXTURE_BU_ADD,32,64)
                .setNormal(new Rect(0,0,32,32))
                .setHover(new Rect(0,32,32,32))
                .setPos(10+offsetX,5+offsetY);
        buttonList.add(bu_add);


        code_list=new ListView(10, fontRenderer, 10, 25, xSize-20, ySize-10-25);
        code_list.setOnItemClickListener((index)->{
            if(index>=0) {
                GuiSingleChip sn = new GuiSingleChip(csc, code_list.getItem(index).title);
                sn.setOnGuiCloseListener(() -> {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiSingleChipList(csc));
                });
                Minecraft.getMinecraft().displayGuiScreen(sn);
            }
        });
        QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","files_get",""));

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id)
        {
            case 1:
                GuiSetName sn=new GuiSetName(csc);
                sn.setOnGuiCloseListener(()->{
                    QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","add_file",sn.tf_name.getText()));
                    Minecraft.getMinecraft().displayGuiScreen(new GuiSingleChipList(csc));
                });
                Minecraft.getMinecraft().displayGuiScreen(sn);
                break;
            default:
                super.actionPerformed(button);
                return;
        }
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBG();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        code_list.draw(new Point(offsetX,offsetY),(double) this.width / this.mc.displayWidth, (double) this.height / this.mc.displayHeight);
    }

    @Override
    public void handleMouseInput() throws IOException {
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth-offsetX;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1-offsetY;

        if(Mouse.isButtonDown(0)) {

        }else
            code_list.mouseUp(i,j,Mouse.getEventButton());

        super.handleMouseInput();
    }

    /*@Override
    protected void keyTyped(char par1, int par2) throws IOException {
        //System.out.println("key:"+par2);
        if(!code_view.onKeyTyped(par1,par2))
            super.keyTyped(par1, par2);
    }*/
}
