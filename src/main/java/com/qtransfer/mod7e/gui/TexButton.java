package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.UnsupportedEncodingException;

public class TexButton extends GuiButton {

    ResourceLocation tex;
    Rect normal,hover;
    int tex_w,tex_h;

    public TexButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public TexButton setTex(ResourceLocation tex,int w, int h){
        this.tex=tex;
        tex_h=h;
        tex_w=w;
        return this;
    }

    public TexButton setNormal(Rect normal) {
        this.normal = normal;
        return this;
    }

    public TexButton setHover(Rect hover) {
        this.hover = hover;
        return this;
    }

    public TexButton setPos(int x,int y){
        this.x=x;
        this.y=y;
        return this;
    }

    public void setText(String text){
        displayString=text;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        //super.drawButton(mc, mouseX, mouseY, partialTicks);
        if (this.visible)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F);

            mc.getTextureManager().bindTexture(tex);
            //GlStateManager.disableDepth();

            int x = mouseX - this.x, y = mouseY - this.y;

            if (x >= 0 && y >= 0 && x < this.width && y < this.height)
            {
                drawScaledCustomSizeModalRect(this.x, this.y, hover.x, hover.y, hover.w, hover.h,width,height,tex_w,tex_h);
            }
            else
            {
                drawScaledCustomSizeModalRect(this.x, this.y, normal.x, normal.y, normal.w, normal.h,width,height,tex_w,tex_h);
            }

            //绘制文本
            FontRenderer fontrenderer = mc.fontRenderer;
            int j = 14737632;

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }
            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
            //GlStateManager.enableDepth();
        }
    }
}
