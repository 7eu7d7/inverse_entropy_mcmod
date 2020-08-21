package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import com.qtransfer.mod7e.transfer.GeneralStack;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiContainerWaveStb extends GUIBase {

    ContainerWaveStb inventory;
    Rect select;
    int page,maxpage;
    int item_per_page;
    Point select_pos;
    GeneralStack select_item;

    GuiTextField tf_num;

    public final int BU_REQUSET=1;
    public final int BU_NEXT=2;
    public final int BU_PREVIOUS=3;

    public GuiContainerWaveStb(ContainerWaveStb inventorySlotsIn)
    {
        super(inventorySlotsIn);
        this.xSize = 400;
        this.ySize = 240;
        inventory=inventorySlotsIn;
    }

    @Override
    public void initGui() {
        super.initGui();

        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        tf_num = new GuiTextField(10, fontRenderer, 30, 10, 30, 10);
        tf_num.setMaxStringLength(64); //设置最大长度,可省略
        tf_num.setFocused(false); //设置是否为焦点
        tf_num.setCanLoseFocus(true); //设置为可以被取消焦点

        tf_num.setText("1");
        addTextField(tf_num);

        TexButton bu_request=new TexButton(BU_REQUSET,0,0,40,20, I18n.format("gui.wave_stb.bu_request"))
                .setTex(GuiElementLoader.TEXTURE_BUTTON,64,128)
                .setNormal(new Rect(0,0,64,64))
                .setHover(new Rect(0,64,64,64))
                .setPos(offsetX+25,offsetY+25);
        buttonList.add(bu_request);

        /*TexButton bu_next=new TexButton(BU_NEXT,0,0,40,20,"")
                .setTex(GuiElementLoader.TEXTURE_BUTTON,64,128)
                .setNormal(new Rect(0,0,64,64))
                .setHover(new Rect(0,64,64,64))
                .setPos(offsetX+80,offsetY+30);
        buttonList.add(bu_next);

        TexButton bu_previous=new TexButton(BU_PREVIOUS,0,0,40,20,"")
                .setTex(GuiElementLoader.TEXTURE_BUTTON,64,128)
                .setNormal(new Rect(0,0,64,64))
                .setHover(new Rect(0,64,64,64))
                .setPos(offsetX+80,offsetY+30);
        buttonList.add(bu_next);*/
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id){
            case BU_REQUSET:{
                if(select_item==null)
                    break;
                int num=Integer.parseInt(tf_num.getText());
                select_item.setCount(num);
                QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","req_items", GeneralStack.packNBT(select_item).toString()));
                QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","req_storage_list",""));
            }break;
        }
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        updateStorage();

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        //GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);

        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG);
        drawScaledCustomSizeModalRect(offsetX, offsetY, 0, 0,979,695,xSize,ySize,979,695);

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG_WSTB);
        drawScaledCustomSizeModalRect(select.x-8, select.y-15, 0, 0,800,523,select.w+16,select.h+30,800,523);

        drawPlayerInv(offsetX+10-2,offsetY+140-2);
        for(int i=0;i<inventory.wse.inventory.getSlots();i++) {
            drawScaledCustomSizeModalRect(offsetX+10-2+(i%9) * 18, offsetY+70-2+(i/9) * 18, 0, 0,80,80,16+4,16+4,80,80);
        }

        for(int i=0;i<inventory.wse.fluid_inventory.size();i++) {
            drawScaledCustomSizeModalRect(offsetX+10-2+(i%9) * 18, offsetY+50-2+(i/9) * 18, 0, 0,80,80,16+4,16+4,80,80);
        }

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG_SELECT);
        int w_items=select.w/20;
        int page_items=Math.min(item_per_page*(page+1),inventory.st_list.size());
        //绘制选框
        int grid_x=(mouseX-select.x)/20;
        int grid_y=(mouseY-select.y)/20;
        if(grid_x>=0 && grid_y>=0 && grid_x<w_items && grid_y*w_items+grid_x<page_items){
            drawScaledCustomSizeModalRect(select.x+grid_x*20, select.y+grid_y*20, 0, 0,64,64,20,20,64,64);
        }

        if(select_pos!=null){
            drawScaledCustomSizeModalRect(select.x+select_pos.x*20, select.y+select_pos.y*20, 0, 0,64,64,20,20,64,64);
        }

        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();

        //绘制物品
        try {
            for (int i = item_per_page * page, u = 0; i < page_items; i++, u++) {
                GeneralStack stack_draw = inventory.st_list.get(i).stack;
                if (stack_draw.fluid)
                    drawFluidStack(stack_draw.fstack, select.x + (u % w_items) * 20 + 2, select.y + (u / w_items) * 20 + 2, TextFormatting.WHITE.toString() + Utils.getShowNum(inventory.st_list.get(i).count));
                else {
                    drawItemStack(stack_draw.stack, select.x + (u % w_items) * 20 + 2, select.y + (u / w_items) * 20 + 2, TextFormatting.WHITE.toString() + Utils.getShowNum(inventory.st_list.get(i).count));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();


    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        //tf_num.drawTextBox();

        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        int w_items=select.w/20;
        int page_items=Math.min(item_per_page*(page+1),inventory.st_list.size());
        //绘制tooltip
        int grid_x=(mouseX-select.x)/20;
        int grid_y=(mouseY-select.y)/20;
        if(grid_x>=0 && grid_y>=0 && grid_x<w_items && grid_y*w_items+grid_x<page_items){
            GeneralStack stack=inventory.st_list.get(page*item_per_page+grid_x+grid_y*w_items).stack;
            if(stack.fluid){
                List<String> tooltip=new ArrayList<String>();
                tooltip.add(I18n.format(stack.fstack.getUnlocalizedName()));
                drawHoveringText(tooltip, mouseX-offsetX, mouseY-offsetY);
            } else {
                drawHoveringText(getItemToolTip(stack.stack),mouseX-offsetX,mouseY-offsetY);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        //tf_num.mouseClicked(mouseX-offsetX,mouseY-offsetY,mouseButton);
        if(inSelectArea(mouseX,mouseY)){
            if(!this.mc.player.inventory.getItemStack().isEmpty()) {
                //添加物品到系统
                ItemStack stack_put = this.mc.player.inventory.getItemStack();
                GeneralStack rest = inventory.wse.addtoStorage(new GeneralStack(stack_put));
                mc.player.inventory.setItemStack(rest.stack);

                //更新gui中物品数
                boolean find = false;
                for (ContainerWaveStb.ItemStackGui ish : inventory.st_list) {
                    if (GeneralStack.stackEqual(stack_put,ish.stack.stack)) {
                        find = true;
                        ish.count+=(stack_put.getCount() - rest.getCount());
                        break;
                    }
                }
                if (!find) {
                    ItemStack its = stack_put.copy();
                    its.setCount(stack_put.getCount() - rest.getCount());
                    inventory.st_list.add(inventory.new ItemStackGui(new GeneralStack(its),stack_put.getCount() - rest.getCount()));
                }
                updateStorage();
            } else {
                int w_items=select.w/20;
                int page_items=Math.min(item_per_page*(page+1),inventory.st_list.size());
                int grid_x=(mouseX-select.x)/20;
                int grid_y=(mouseY-select.y)/20;
                if(grid_x>=0 && grid_y>=0 && grid_y*w_items+grid_x<page_items){
                    select_pos=new Point(grid_x,grid_y);
                    select_item=inventory.st_list.get(page*item_per_page+grid_x+grid_y*w_items).stack.copy();
                } else {
                    select_pos=null;
                    select_item=null;
                }
            }
        }else
            super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void updateStorage(){
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        select=new Rect(offsetX+180+8,offsetY+20+8,xSize-16-190,180);
        item_per_page=select.w/20*select.h/20;
        maxpage=inventory.st_list.size()/item_per_page;
    }

    public boolean inSelectArea(int x,int y){
        return select.inRect(x,y);
    }
}
