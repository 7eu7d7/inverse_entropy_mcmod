package com.qtransfer.mod7e.gui.widget;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.utils.PythonSyntaxHighlighter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.text.StrBuilder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.python.antlr.ast.Str;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class GuiCodeView extends Gui {
    private final int id;
    private final FontRenderer fontRenderer;
    public int x;
    public int y;
    /** The width of this text field. */
    public int width;
    public int height;

    int tx,ty;
    int line_h;
    int line_show;
    Rect draw_area;
    Point select_start=new Point();
    Point select_end=new Point();

    public boolean visible=true;

    public ArrayList<String> texts=new ArrayList<String>();

    PythonSyntaxHighlighter highlighter=new PythonSyntaxHighlighter();
    String[] texts_draw=new String[0];

    Rect range_linenum;

    public GuiCodeView(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height)
    {
        this.id = componentId;
        this.fontRenderer = fontrendererObj;
        this.x = x;
        this.y = y;
        this.width = par5Width;
        this.height = Math.max(par6Height, fontRenderer.FONT_HEIGHT * 2);
        line_h=fontRenderer.FONT_HEIGHT;
        texts.add("");

        range_linenum=new Rect(x,y,20,height);
        setPadding(3+range_linenum.w,0,3,0);
    }

    public void draw(Point raw_pos,double rate_w,double rate_h){
        if (!this.getVisible())
            return;

        int line_count=0;

        //draw background
        drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
        drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        updateTranslate();

        GlStateManager.pushMatrix();

        GlStateManager.translate(-tx,-ty,0);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)((raw_pos.x+draw_area.x)/rate_w),(int)((raw_pos.y+draw_area.y)/rate_h), (int) (draw_area.w/rate_w), (int) (draw_area.h/rate_h));

        int start_line=getLineIndex(ty);
        //draw text
        for(int i=start_line;i<Math.min(texts_draw.length,start_line+line_show);i++){
            fontRenderer.drawString(texts_draw[i],draw_area.x,draw_area.y+i*line_h,0xFFFFFFFF);
        }

        //draw selection
        GlStateManager.pushMatrix();
        GlStateManager.translate(draw_area.x,draw_area.y,0);
        drawSelectionBox(select_start,select_end);
        GlStateManager.popMatrix();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GlStateManager.popMatrix();

        //draw line number
        GlStateManager.pushMatrix();
        GlStateManager.translate(0,-ty,0);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int)((raw_pos.x+range_linenum.x)/rate_w),(int)((raw_pos.y+range_linenum.y)/rate_h), (int) (range_linenum.w/rate_w), (int) (range_linenum.h/rate_h));
        drawLineNumber(range_linenum);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }

    private void drawSelectionBox(Point start, Point end)
    {
        if(start.y==end.y){
            drawSelectionBox(start.y,start.x,end.x);
        } else {
            drawSelectionBox(start.y,start.x,texts.get(start.y).length()); //start
            for(int i=start.y+1;i<end.y;i++)
                drawSelectionBox(i,0,texts.get(i).length()); //mid
            drawSelectionBox(end.y,0,end.x); //end
        }
    }

    private void drawSelectionBox(int line, int start,int end)
    {
        String line_str=texts.get(line);
        //int sx=fontRenderer.getStringWidth(line_str.substring(0,start));
        //int ex=fontRenderer.getStringWidth(line_str.substring(0,end));
        int sx=fontRenderer.drawString(line_str.substring(0,start),0,-100,0xFFFFFFFF);
        int ex=fontRenderer.drawString(line_str.substring(0,end),0,-100,0xFFFFFFFF);
        int sy=line*line_h;
        int ey=line*line_h+fontRenderer.FONT_HEIGHT;

        if(start==end) {
            Gui.drawRect(sx,sy,sx+1,ey,0xffffffff);
        } else {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.enableColorLogic();
            GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
            bufferbuilder.pos((double) sx, (double) ey, 0.0D).endVertex();
            bufferbuilder.pos((double) ex, (double) ey, 0.0D).endVertex();
            bufferbuilder.pos((double) ex, (double) sy, 0.0D).endVertex();
            bufferbuilder.pos((double) sx, (double) sy, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.disableColorLogic();
            GlStateManager.enableTexture2D();
        }
    }

    public void drawLineNumber(Rect range){
        int start_line=getLineIndex(ty);
        int end_line=Math.min(texts_draw.length,start_line+line_show);

        Gui.drawRect(range.x,range.y+start_line*line_h,range.x+range.w,range.y+range.h+start_line*line_h+line_h,0x7f7f7fff);
        Gui.drawRect(range.x+range.w-1,range.y+start_line*line_h,range.x+range.w,range.y+range.h+start_line*line_h+line_h,0xffffffbf);

        //draw text
        for(int i=start_line;i<end_line;i++){
            fontRenderer.drawString((i+1)+"",range.x,range.y+i*line_h,0x66ccffff);
        }

    }


    public int getLineIndex(int offy){ //相对draw_area
        return Math.min((offy)/line_h,Math.max(texts.size()-1,0));
    }

    public Point getPosition(int x,int y){ //相对draw_area
        int line=getLineIndex(y+ty);
        return new Point(fontRenderer.trimStringToWidth(texts.get(line), x+tx).length(),line);
    }

    public void updateTranslate(){
        //int len=fontRenderer.getStringWidth(texts.get(select_end.y).substring(0,select_end.x));
        if(texts.size()<=0)
            texts.add("");

        int len=fontRenderer.drawString(texts.get(select_end.y).substring(0,select_end.x),0,-100,0xFFFFFFFF);
        if(len>tx+draw_area.w-5){
            tx=len-draw_area.w+2;
        }else if(len<tx+5){
            tx=Math.max(0,len-5);
        }

        if(line_h*(select_end.y+1)>ty+draw_area.h){
            ty=line_h*(select_end.y+1)-draw_area.h;
        }else if(line_h*select_end.y<ty){
            ty=Math.max(0,line_h*select_end.y);
        }
    }

    private Point left(Point pos){
        if(pos.x==0) {
            if(pos.y==0)
                return new Point(0,0);
            else
                return new Point(texts.get(pos.y-1).length(),pos.y-1);
        }else
            return new Point(pos.x-1,pos.y);
    }

    private Point right(Point pos){
        if(pos.x==texts.get(pos.y).length()) {
            if(pos.y==texts.size()-1)
                return pos;
            else
                return new Point(0,pos.y+1);
        }else
            return new Point(pos.x+1,pos.y);
    }

    private Point up(Point pos){
        if(pos.y==0)
            return pos;
        else {
            int ux=Math.min(pos.x,texts.get(pos.y-1).length());
            return new Point(ux,pos.y-1);
        }
    }

    private Point down(Point pos){
        if(pos.y>=texts.size()-1)
            return pos;
        else {
            int ux=Math.min(pos.x,texts.get(pos.y+1).length());
            return new Point(ux,pos.y+1);
        }
    }

    public void updateDrawText(){
        String str=getText();
        texts_draw=highlighter.process(str).split("\n");
    }

    public void insertText(String text){
        text=normalize(text);

        if(!select_start.equals(select_end))
            removeText(select_start,select_end);

        if(select_start.y==texts.size())
            texts.add("");

        //int end_x=texts.get(select_start.y).length()-text.lastIndexOf("\n")+1;

        String line=texts.remove(select_start.y);
        line=new StringBuilder(line).insert(select_start.x,text).toString();
        for (String str:line.split("\n")){
            texts.add(select_start.y++,str);
        }
        select_start.y--;
        int idx_ln=text.lastIndexOf("\n");
        if(idx_ln==-1){
            select_start.x+=text.length();
        }
        else
            select_start.x=text.length()-(idx_ln+1);
        select_end=new Point(select_start);
        updateDrawText();
    }

    public Point removeText(Point pos_start,Point pos_end){
        String strat=texts.get(pos_start.y).substring(0,pos_start.x);
        String end=texts.get(pos_end.y).substring(pos_end.x);

        for(int i=pos_start.y;i<=pos_end.y;pos_end.y--){
            texts.remove(i);
        }
        //select_end.y++;

        texts.add(pos_start.y,strat+end);
        updateDrawText();
        return pos_start;
        //select_end.x=select_start.x;
    }

    public String getSelectedText(){

        String end=texts.get(select_end.y).substring(0,select_end.x);
        if(select_start.y==select_end.y)
            return texts.get(select_start.y).substring(select_start.x,select_end.x);
        else {
            StringBuilder res=new StringBuilder(texts.get(select_start.y).substring(select_start.x)).append("\n");
            for(int i=select_start.y+1;i<select_end.y;i++){
                res.append(texts.get(i)).append("\n");
            }
            res.append(texts.get(select_end.y).substring(0,select_end.x));
            return res.toString();
        }
    }

    public String normalize(String text){
        return text.replace("\t","    ");
    }

    public void mouseDown(int mx,int my,int type){
        //System.out.println("down"+type+" mx"+mx+" my"+my);
        //System.out.println(draw_area.toString());
        if(!draw_area.inRect(mx,my))
            return;
        if(type==0){
            select_start=getPosition(mx-draw_area.x,my-draw_area.y);
            select_end=new Point(select_start);
        }
    }

    public void mouseDrag(int mx,int my,int type){
        //System.out.println("down"+type+" mx"+mx+" my"+my);
        //System.out.println(draw_area.toString());
        /*if(!draw_area.inRect(mx,my))
            return;*/
        if(type==-1){
            select_end=getPosition(mx-draw_area.x,my-draw_area.y);
            checkSelection();
        }
    }

    public void mouseUp(int mx,int my,int type){
        if(!draw_area.inRect(mx,my))
            return;
        if(type==0){
            select_end=getPosition(mx-draw_area.x,my-draw_area.y);
            checkSelection();
        }
    }

    public void mouseScroll(int dist){
        if(dist>0) {
            select_start = up(select_start);
            select_end = new Point(select_start);
        }else if(dist<0){
            select_start = down(select_start);
            select_end = new Point(select_start);
        }
    }

    public boolean onKeyTyped(char typedChar, int keyCode){
        //System.out.println(keyCode);
        if (GuiScreen.isKeyComboCtrlA(keyCode))
        {
            select_start=new Point(0,0);
            select_end=new Point(texts.size()-1,texts.get(texts.size()-1).length());
            return true;
        }
        else if (GuiScreen.isKeyComboCtrlC(keyCode))
        {
            GuiScreen.setClipboardString(getSelectedText());
            return true;
        }
        else if (GuiScreen.isKeyComboCtrlV(keyCode))
        {
            insertText(GuiScreen.getClipboardString());
            return true;
        }
        else if (GuiScreen.isKeyComboCtrlX(keyCode))
        {
            GuiScreen.setClipboardString(getSelectedText());
            removeText(select_start,select_end);
            return true;
        }
        switch (keyCode){
            case 14:{ //back space
                if(select_end.equals(select_start)){
                    select_start=removeText(left(select_start),select_end);
                } else {
                    select_start=removeText(select_start,select_end);
                }

                select_end=new Point(select_start);
                return true;
            }
            case 28:{ // \n
                if(select_start.x==texts.get(select_start.y).length()){
                    texts.add(++select_start.y,"");
                    select_start.x=0;
                    select_end=new Point(select_start);
                    updateDrawText();
                }else
                    insertText("\n");
                return true;
            }
            case 200:{ //up
                select_start=up(select_start);
                select_end=new Point(select_start);
                return true;
            }
            case 208:{ //down
                select_start=down(select_start);
                select_end=new Point(select_start);
                return true;
            }
            case 203:{ //left
                select_start=left(select_start);
                select_end=new Point(select_start);
                return true;
            }
            case 205:{ //right
                select_start=right(select_start);
                select_end=new Point(select_start);
                return true;
            }
            default:{
                if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    insertText("" + typedChar);
                    return true;
                }
            }
        }

        return false;
    }

    public void setText(String text){
        text=normalize(text);
        texts.clear();
        texts.addAll(Arrays.asList(text.split("\n")));
        updateDrawText();
    }

    public String getText(){
        StringBuilder str=new StringBuilder();
        for(String line:texts){
            str.append(line).append("\n");
        }
        //str.deleteCharAt(str.length()-1);
        return str.toString();
    }

    public void setPadding(int left,int top,int right,int bottom){
        draw_area=new Rect(x+left,y+top,width-right-left,height-bottom-top);
        line_show=(int)Math.ceil((double) draw_area.h/line_h);
    }

    public void checkSelection(){
        if(select_end.y<select_start.y || (select_end.y==select_start.y && select_end.x<select_start.x)){
            Point tmp=select_start;
            select_start=select_end;
            select_end=tmp;
        }
    }

    public boolean getVisible()
    {
        return this.visible;
    }

    public void setVisible(boolean isVisible)
    {
        this.visible = isVisible;
    }
}
