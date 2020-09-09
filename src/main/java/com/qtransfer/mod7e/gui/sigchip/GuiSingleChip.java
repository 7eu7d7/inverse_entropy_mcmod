package com.qtransfer.mod7e.gui.sigchip;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.gui.ContainerBase;
import com.qtransfer.mod7e.gui.ContainerSingleChip;
import com.qtransfer.mod7e.gui.GUIBase;
import com.qtransfer.mod7e.gui.widget.GuiCodeView;
import com.qtransfer.mod7e.items.SingleChipItem;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.client.gui.GuiListButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class GuiSingleChip extends GUIBase {
    //GuiMultiLineTextField tf_code;
    public GuiCodeView code_view;
    ContainerSingleChip csc;
    public boolean initok=false;

    public GuiSingleChip(ContainerBase inventorySlotsIn, String file_name) {
        super(inventorySlotsIn);
        this.xSize = 220;
        csc=(ContainerSingleChip)inventorySlotsIn;
        csc.gui_chip=this;
        QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","select_file", file_name));
    }

    @Override
    public void initGui() {
        super.initGui();

        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        /*tf_code = new GuiMultiLineTextField(10, fontRenderer, 10, 10, xSize-20, ySize-20);
        tf_code.setMaxStringLength(1024); //设置最大长度,可省略
        tf_code.setFocused(false); //设置是否为焦点
        tf_code.setCanLoseFocus(true); //设置为可以被取消焦点*/
        code_view=new GuiCodeView(10, fontRenderer, 10, 10, xSize-20, ySize-20);
        QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","code_get",""));
        //tf_code.setText(csc.chip.readFromFile(SingleChipItem.file_name));
        //addTextField(tf_name);

    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBG();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        code_view.draw(new Point(offsetX,offsetY),(double) this.width / this.mc.displayWidth, (double) this.height / this.mc.displayHeight);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        //tf_code.mouseClicked(mouseX-offsetX,mouseY-offsetY,mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        //System.out.println("x:"+ Mouse.getEventX()+" y:"+ Mouse.getEventY()+" type:"+ Mouse.getEventButton());
        //System.out.println("wheel:"+ Mouse.getEventDWheel());
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth-offsetX;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1-offsetY;

        if(Mouse.isButtonDown(0)) {
            if(Mouse.getEventButton()==-1)
                code_view.mouseDrag(i, j, Mouse.getEventButton());
            else
                code_view.mouseDown(i, j, Mouse.getEventButton());
        }else
            code_view.mouseUp(i,j,Mouse.getEventButton());

        if(Mouse.getEventDWheel()!=0)
            code_view.mouseScroll(Mouse.getEventDWheel()/120);

        super.handleMouseInput();
    }

    @Override
    public void onGuiClosed() {
        if(initok) {
            //System.out.println("save " + code_view.getText());
            QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container", "code_save", code_view.getText()));
        }
        super.onGuiClosed();
    }

    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        //System.out.println("key:"+par2);
        if(!code_view.onKeyTyped(par1,par2))
            super.keyTyped(par1, par2);
    }
}
