package com.qtransfer.mod7e.gui.widget;

import com.qtransfer.mod7e.Rect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
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

    public final int LINE_H=13;

    public FontRenderer fontRenderer;
    public OnItemClick oic;

    public ArrayList<ListItem> items=new ArrayList<ListItem>();

    public ListView(int componentId, int x, int y, int width, int height)
    {
        this.id = componentId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Point raw_pos, double rate_w, double rate_h){
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)((raw_pos.x+draw_area.x)/rate_w),(int)((raw_pos.y+draw_area.y)/rate_h), (int) (draw_area.w/rate_w), (int) (draw_area.h/rate_h));

        for(int i=py/LINE_H;i<Math.min(items.size(), Math.ceil((py+draw_area.h)/(float)LINE_H));i++){
            drawItem(new Rect(draw_area.x,draw_area.y+py,draw_area.w,draw_area.h), items.get(i));
            drawLine(py+i*13-1,0xffffffbf);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public void drawLine(int posy,int color){
        drawRect(draw_area.x,draw_area.y+posy,draw_area.w,1, color);
    }

    public void drawItem(Rect rect, ListItem item){
        drawRect(rect.x, rect.y, rect.x+rect.w, rect.y+rect.h, 0xff333333);

        Minecraft.getMinecraft().getTextureManager().bindTexture(item.icon);
        drawScaledCustomSizeModalRect(rect.x+1, rect.y+1, 0, 0,64,64,10,10,64,64);

        fontRenderer.drawString(item.title,rect.x+14,rect.y+4,0xFFFFFFFF);
    }

    public ListItem getItem(int idx){
        return items.get(idx);
    }

    public void setPadding(int left,int top,int right,int bottom){
        draw_area=new Rect(x+left,y+top,width-right-left,height-bottom-top);
    }

    public void mouseDown(int mx,int my,int type){
        //System.out.println("down"+type+" mx"+mx+" my"+my);
        //System.out.println(draw_area.toString());
        if(!draw_area.inRect(mx,my))
            return;
        if(type==0){
            if(oic!=null) {
                int idx=(my - draw_area.y)/12;
                oic.onClick(idx>=items.size()?-1:idx);
            }
        }
    }

    public void setOnItemClickListener(OnItemClick oic) {
        this.oic = oic;
    }

    public class ListItem{
        ResourceLocation icon;
        String title;

        public ListItem(ResourceLocation icon, String title){
            this.icon=icon;
            this.title=title;
        }
    }

    public interface OnItemClick{
        void onClick(int index);
    }
}
