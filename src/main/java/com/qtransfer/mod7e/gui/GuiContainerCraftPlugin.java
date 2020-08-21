package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;

import java.io.IOException;

public class GuiContainerCraftPlugin extends GUIBase{
    ContainerCraftPlugin ccp;
    TexButton bu_add_stuff,bu_order,bu_add_fluid,bu_sw_result;

    public GuiContainerCraftPlugin(ContainerBase inventorySlotsIn) {
        super(inventorySlotsIn);
        this.xSize = 176;
        this.ySize = 240;
        ccp=(ContainerCraftPlugin)inventorySlotsIn;
    }

    @Override
    public void initGui() {
        super.initGui();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        bu_add_stuff=new TexButton(1,0,0,16,16,"")
                .setTex(GuiElementLoader.TEXTURE_BU_ADD,32,64)
                .setNormal(new Rect(0,0,32,32))
                .setHover(new Rect(0,32,32,32));
        buttonList.add(bu_add_stuff);

        bu_add_fluid=new TexButton(2,0,0,16,16,"")
                .setTex(GuiElementLoader.TEXTURE_BU_ADD,32,64)
                .setNormal(new Rect(0,0,32,32))
                .setHover(new Rect(0,32,32,32));
        buttonList.add(bu_add_fluid);

        bu_sw_result=new TexButton(10,0,0,28,16,ccp.craft.fluid ? I18n.format("gui.craft_plugin.res_fluid"):I18n.format("gui.craft_plugin.res_item"))
                .setTex(GuiElementLoader.TEXTURE_BUTTON,64,128)
                .setNormal(new Rect(0,0,64,64))
                .setHover(new Rect(0,64,64,64))
                .setPos(10+offsetX,12+offsetY);
        buttonList.add(bu_sw_result);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id)
        {
            case 1:
                ccp.addItemStuff();
                QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","item_slot_add",""));
                break;
            case 2:
                ccp.addFluidStuff();
                QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","fluid_slot_add",""));
                break;
            case 10:
                ccp.switchResult();
                bu_sw_result.setText(ccp.craft.fluid ? I18n.format("gui.craft_plugin.res_fluid"):I18n.format("gui.craft_plugin.res_item"));
                QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","set_result",ccp.craft.fluid?"fluid":"item"));
                //System.out.println(ccp.result_fluid.enable);
                break;
            default:
                super.actionPerformed(button);
                return;
        }
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        //GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG);
        drawScaledCustomSizeModalRect(offsetX, offsetY, 0, 0,979,695,xSize,ySize,979,695);

        //绘制玩家物品
        drawPlayerInv(offsetX-2+8,offsetY-2+150);

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE);
        int startx=9-2,starty=20;
        //result
        ccp.result_item.xPos=70;
        ccp.result_item.yPos=starty;
        ccp.result_fluid.x=70;
        ccp.result_fluid.y=starty;

        //drawScaledCustomSizeModalRect(offsetX+30-2, starty+offsetY-2, 0, 0,80,80,16+4,16+4,80,80);
        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_SLOT_CIRCLE);
        drawScaledCustomSizeModalRect(offsetX+70-8, starty+offsetY-8, 0, 0,64,64,32,32,64,64);

        drawStuff(partialTicks,mouseX,mouseY,startx,starty);
    }

    public void drawStuff(float partialTicks, int mouseX, int mouseY,int startx,int starty){
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE);
        //stuffs
        //item
        starty+=36;
        int i=0;
        for(;i<ccp.craft.inventory.getSlots();i++) {
            ccp.slot_stuffs.get(i).xPos=startx+(i%ccp.row_count)*18;
            ccp.slot_stuffs.get(i).yPos=starty+(i/ccp.row_count)*18;
            drawScaledCustomSizeModalRect(offsetX+startx+(i%ccp.row_count)*18-2, offsetY+starty+(i/ccp.row_count)*18-2, 0, 0,80,80,16+4,16+4,80,80);
        }
        bu_add_stuff.setPos(offsetX+startx+(i%ccp.row_count)*18, offsetY+starty+(i/ccp.row_count)*18);

        //fluid
        starty+=Math.ceil((ccp.craft.inventory.getSlots()+1)/9f)*18;
        i=0;
        for(;i<ccp.slot_stuffs_fluid.size();i++) {
            ccp.slot_stuffs_fluid.get(i).x=startx+(i%ccp.row_count)*18;
            ccp.slot_stuffs_fluid.get(i).y=starty+(i/ccp.row_count)*18;
            drawScaledCustomSizeModalRect(offsetX+startx+(i%ccp.row_count)*18-2, offsetY+starty+(i/ccp.row_count)*18-2, 0, 0,80,80,16+4,16+4,80,80);
        }
        bu_add_fluid.setPos(offsetX+startx+(i%ccp.row_count)*18, offsetY+starty+(i/ccp.row_count)*18);
    }

}
