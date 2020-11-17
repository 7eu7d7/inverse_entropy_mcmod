package com.qtransfer.mod7e.gui.widget;

import com.qtransfer.mod7e.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ListView extends Gui {
    private final int id;
    public int x;
    public int y;
    /** The width of this text field. */
    public int width;
    public int height;

    Rect draw_area;
    int py;

    public final int LINE_H=15;

    public FontRenderer fontRenderer;
    public OnItemClick oic;

    public ArrayList<ListItem> items=new ArrayList<ListItem>();

    public ListView(int componentId, FontRenderer fontrendererObj, int x, int y, int width, int height)
    {
        this.id = componentId;
        this.fontRenderer = fontrendererObj;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setPadding(3,0,3,0);
    }

    public void draw(Point raw_pos, double rate_w, double rate_h){
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)((raw_pos.x+draw_area.x)/rate_w),(int)((raw_pos.y+draw_area.y)/rate_h), (int) (draw_area.w/rate_w), (int) (draw_area.h/rate_h));

        //RenderHelper.disableStandardItemLighting();
        //RenderHelper.enableGUIStandardItemLighting();

        for(int i=py/LINE_H;i<Math.min(items.size(), Math.ceil((py+draw_area.h)/(float)LINE_H));i++){
            drawItem(new Rect(draw_area.x,draw_area.y+py+i*LINE_H,draw_area.w,LINE_H), items.get(i));
            drawLine(py+i*LINE_H-1,0xffffffbf);
        }

        //RenderHelper.enableStandardItemLighting();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public void drawLine(int posy,int color){
        drawRect(draw_area.x,draw_area.y+posy,draw_area.x+draw_area.w,draw_area.y+posy+1, color);
    }

    public void drawItem(Rect rect, ListItem item){
        drawRect(rect.x, rect.y, rect.x+rect.w, rect.y+rect.h, 0xff333333);

        GlStateManager.color(1,1,1,1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(item.icon);
        drawScaledCustomSizeModalRect(rect.x+2, rect.y+2, 0, 0,64,64,10,10,64,64);

        fontRenderer.drawString(item.title,rect.x+15,rect.y+4,0xFFFFFFFF);
    }

    public ListItem getItem(int idx){
        return items.get(idx);
    }

    public void setPadding(int left,int top,int right,int bottom){
        draw_area=new Rect(x+left,y+top,width-right-left,height-bottom-top);
    }

    public void mouseUp(int mx,int my,int type){
        //System.out.println("down"+type+" mx"+mx+" my"+my);
        //System.out.println(draw_area.toString());
        if(!draw_area.inRect(mx,my))
            return;
        if(type==0){
            if(oic!=null) {
                int idx=(my - draw_area.y)/LINE_H;
                oic.onClick(idx>=items.size()?-1:idx);
            }
        }
    }

    public void addItem(ResourceLocation icon, String title){
        items.add(new ListItem(icon,title));
    }

    public void setOnItemClickListener(OnItemClick oic) {
        this.oic = oic;
    }

    public class ListItem{
        ResourceLocation icon;
        public String title;

        public ListItem(ResourceLocation icon, String title){
            this.icon=icon;
            this.title=title;
        }
    }

    public interface OnItemClick{
        void onClick(int index);
    }
}
