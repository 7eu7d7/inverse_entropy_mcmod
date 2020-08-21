package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Constant;
import com.qtransfer.mod7e.Rect;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainerH2ODe extends GUIBase {

    ContainerH2ODe inventory;

    public GuiContainerH2ODe(ContainerH2ODe inventorySlotsIn)
    {
        super(inventorySlotsIn);
        this.xSize = 176;
        this.ySize = 183;
        inventory=inventorySlotsIn;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        drawBG();
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_SUN_BG);
        drawScaledCustomSizeModalRect(offsetX+55+2, offsetY+15, 0, 0,64,64,16,16,64,64);

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_SUN);
        int[] area={0,64, 64,64,64,64-64*inventory.tile.progress/inventory.tile.per_ticks, 0,64-64*inventory.tile.progress/inventory.tile.per_ticks};
        drawTexturePoly(new Rect(offsetX+55+2, offsetY+15,16,16),new Rect(0,0,64,64),area,64,64);
        //drawScaledCustomSizeModalRect(offsetX+55+2, offsetY+15, 0, 0,64,64,16,16,64,64);

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_ARROW);
        drawScaledCustomSizeModalRect(offsetX+5+4, offsetY+37, 0, 0,50,70,12,18,50,70);
        drawScaledCustomSizeModalRect(offsetX+80+4, offsetY+37, 0, 0,50,70,12,18,50,70);
        drawScaledCustomSizeModalRect(offsetX+130+4, offsetY+37, 0, 0,50,70,12,18,50,70);

        drawSlotBG();

        //绘制流体槽
        draw_flutank(offsetX+30,offsetY+20,mouseX,mouseY,inventory.tile.tank);
        draw_flutank(offsetX+100,offsetY+20,mouseX,mouseY,inventory.tile.tank_o2);
        draw_flutank(offsetX+150,offsetY+20,mouseX,mouseY,inventory.tile.tank_h2);
    }


}
