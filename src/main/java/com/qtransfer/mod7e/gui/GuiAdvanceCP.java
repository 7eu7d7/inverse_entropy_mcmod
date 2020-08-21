package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.QuantumTransfer;
import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.ArrayList;

public class GuiAdvanceCP extends GuiContainerCraftPlugin{
    ContainerAdvanceCP cacp;
    boolean clicked=false;

    public GuiAdvanceCP(ContainerBase inventorySlotsIn) {
        super(inventorySlotsIn);
        cacp=(ContainerAdvanceCP)inventorySlotsIn;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        clicked=true;
    }

    @Override
    public void drawStuff(float partialTicks, int mouseX, int mouseY, int startx, int starty) {
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE);
        //stuffs
        //item
        int sendh=12;
        starty+=30;
        int ytmp=starty;
        int i=0;
        for(;i<ccp.craft.inventory.getSlots();i++) {
            ccp.slot_stuffs.get(i).xPos=startx+(i%ccp.row_count)*18;
            ccp.slot_stuffs.get(i).yPos=starty+(i/ccp.row_count)*(18+sendh)+sendh;
            drawScaledCustomSizeModalRect(offsetX+startx+(i%ccp.row_count)*18-2, offsetY+starty+sendh+(i/ccp.row_count)*(18+sendh)-2, 0, 0,80,80,16+4,16+4,80,80);
        }
        bu_add_stuff.setPos(offsetX+startx+(i%ccp.row_count)*18, offsetY+starty+(i/ccp.row_count)*(18+sendh)+sendh);

        //fluid
        starty+=Math.ceil((ccp.craft.inventory.getSlots()+1)/9f)*(18+sendh);
        i=0;
        for(;i<ccp.slot_stuffs_fluid.size();i++) {
            ccp.slot_stuffs_fluid.get(i).x=startx+(i%ccp.row_count)*18;
            ccp.slot_stuffs_fluid.get(i).y=starty+(i/ccp.row_count)*(18+sendh)+sendh;
            drawScaledCustomSizeModalRect(offsetX+startx+(i%ccp.row_count)*18-2, offsetY+starty+sendh+(i/ccp.row_count)*(18+sendh)-2, 0, 0,80,80,16+4,16+4,80,80);
        }
        bu_add_fluid.setPos(offsetX+startx+(i%ccp.row_count)*18, offsetY+starty+(i/ccp.row_count)*(18+sendh)+sendh);

        //buttons
        starty=ytmp;
        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BU_ADD_SEND);
        i=0;
        for(;i<ccp.craft.inventory.getSlots();i++) {
            int bux=offsetX+startx+(i%ccp.row_count)*18+4,buy=offsetY+starty+(i/ccp.row_count)*(18+sendh)+1;
            if(new Rect(bux,buy,8,8).inRect(mouseX,mouseY)) {
                drawScaledCustomSizeModalRect(bux, buy, 0, 64, 32, 32, 8, 8, 32, 96);
                if(clicked){
                    Minecraft.getMinecraft().displayGuiScreen(new GuiACPSendName(cacp,i));
                    //cacp.player.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_BLOCK_SHAPER, cacp.player.getEntityWorld(), cacp.pos.getX(), cacp.pos.getY(), cacp.pos.getZ());
                }
            }else if(cacp.acp.item_send.get(i).length()>0)
                drawScaledCustomSizeModalRect(bux, buy, 0, 0,32,32,8,8,32,96);
            else
                drawScaledCustomSizeModalRect(bux, buy, 0, 32,32,32,8,8,32,96);
        }

        //fluid
        starty+=Math.ceil((ccp.craft.inventory.getSlots()+1)/9f)*(18+sendh);
        i=0;
        for(;i<ccp.slot_stuffs_fluid.size();i++) {
            int bux=offsetX+startx+(i%ccp.row_count)*18+4,buy=offsetY+starty+(i/ccp.row_count)*(18+sendh)+1;
            if(new Rect(bux,buy,8,8).inRect(mouseX,mouseY)) {
                drawScaledCustomSizeModalRect(bux, buy, 0, 64, 32, 32, 8, 8, 32, 96);
                if(clicked){
                    Minecraft.getMinecraft().displayGuiScreen(new GuiACPSendName(cacp,i+100));
                    //cacp.player.openGui(QuantumTransfer.instance, GuiElementLoader.GUI_BLOCK_SHAPER, cacp.player.getEntityWorld(), cacp.pos.getX(), cacp.pos.getY(), cacp.pos.getZ());
                }
            }else if(cacp.acp.fluid_send.get(i).length()>0)
                drawScaledCustomSizeModalRect(bux, buy, 0, 0,32,32,8,8,32,96);
            else
                drawScaledCustomSizeModalRect(bux, buy, 0, 32,32,32,8,8,32,96);
        }

        clicked=false;
    }
}
