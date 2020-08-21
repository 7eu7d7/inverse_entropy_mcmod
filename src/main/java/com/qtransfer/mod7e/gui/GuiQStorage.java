package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import com.qtransfer.mod7e.transfer.GeneralStack;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiQStorage extends GUIBase{
    ContainerQStorage cqs;
    Rect select;
    int page,maxpage;
    int item_per_page;
    Point select_pos;
    GeneralStack select_item;

    Rect rect_fluidadd;


    public GuiQStorage(ContainerBase inventorySlotsIn) {
        super(inventorySlotsIn);
        cqs= (ContainerQStorage) inventorySlotsIn;
        cqs.guist=this;
        this.xSize = 220;
        this.ySize = 240;


    }

    @Override
    public void initGui() {
        super.initGui();
        updateStorage();
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBG();
        drawSlotBG();

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_FLUID_IO);
        drawScaledCustomSizeModalRect(rect_fluidadd.x, rect_fluidadd.y, 0, 0,32,32,rect_fluidadd.w,rect_fluidadd.h,32,32);

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG_WSTB);
        drawScaledCustomSizeModalRect(select.x-8, select.y-15, 0, 0,800,523,select.w+16,select.h+30,800,523);

        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG_SELECT);
        int w_items=select.w/20;
        int page_items=Math.min(item_per_page*(page+1),cqs.st_list.size());
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
                GeneralStack stack_draw = cqs.st_list.get(i);
                if (stack_draw.fluid)
                    drawFluidStack(stack_draw.fstack, select.x + (u % w_items) * 20 + 2, select.y + (u / w_items) * 20 + 2, TextFormatting.WHITE.toString() + Utils.getShowNum(cqs.st_list.get(i).getCount()));
                else {
                    drawItemStack(stack_draw.stack, select.x + (u % w_items) * 20 + 2, select.y + (u / w_items) * 20 + 2, TextFormatting.WHITE.toString() + Utils.getShowNum(cqs.st_list.get(i).getCount()));
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

        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        int w_items=select.w/20;
        int page_items=Math.min(item_per_page*(page+1),cqs.st_list.size());
        //绘制tooltip
        int grid_x=(mouseX-select.x)/20;
        int grid_y=(mouseY-select.y)/20;
        if(grid_x>=0 && grid_y>=0 && grid_x<w_items && grid_y*w_items+grid_x<page_items){
            GeneralStack stack=cqs.st_list.get(page*item_per_page+grid_x+grid_y*w_items);
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
            int w_items=select.w/20;
            int page_items=Math.min(item_per_page*(page+1),cqs.st_list.size());
            int grid_x=(mouseX-select.x)/20;
            int grid_y=(mouseY-select.y)/20;
            if(grid_x>=0 && grid_y>=0 && grid_y*w_items+grid_x<page_items){
                select_pos=new Point(grid_x,grid_y);
                select_item =cqs.st_list.get(page*item_per_page+grid_x+grid_y*w_items).copy();
            } else {
                select_pos=null;
                select_item=null;
            }

            if(this.mc.player.inventory.getItemStack().isEmpty() ||
                    (select_item!=null && select_item.fluid && mc.player.inventory.getItemStack().hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null))) {
                //取出物品
                if(select_item==null)
                    return;
                GeneralStack stack_take=select_item.copy();
                if(!stack_take.fluid)
                    stack_take.setCount(64);

                QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","take_storage",stack_take.serializeNBT().toString()));
                select_pos=null;
                select_item=null;
            } else {
                //添加物品到系统
                ItemStack stack_put = this.mc.player.inventory.getItemStack();
                mc.player.inventory.setItemStack(ItemStack.EMPTY);

                QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","add_storage",new GeneralStack(stack_put).serializeNBT().toString()));
                select_pos=null;
                select_item=null;
            }
        }else if(rect_fluidadd.inRect(mouseX,mouseY)){
            //添加流体到系统
            ItemStack stack_put = this.mc.player.inventory.getItemStack();
            if(stack_put.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null)){
                QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("container","add_fluid",new GeneralStack(stack_put).serializeNBT().toString()));
            }
            select_pos=null;
            select_item=null;
        }else
            super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void updateStorage(){
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        select=new Rect(offsetX+8,offsetY+20+8,xSize-16,100);
        item_per_page=select.w/20*select.h/20;
        maxpage=cqs.st_list.size()/item_per_page;

        rect_fluidadd=new Rect(offsetX+180, offsetY+140, 20, 20);
    }

    public boolean inSelectArea(int x,int y){
        return select.inRect(x,y);
    }
}
