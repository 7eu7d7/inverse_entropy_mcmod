package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.energy.IQEnergy;
import com.qtransfer.mod7e.proxy.BasePacket;
import com.qtransfer.mod7e.proxy.QNetworkManager;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUIBase extends GuiContainer {

    ContainerBase container;
    List<GuiTextField> tflist=new ArrayList<GuiTextField>();

    public GUIBase(ContainerBase inventorySlotsIn)
    {
        super(inventorySlotsIn);
        this.container=inventorySlotsIn;
        this.xSize = 176;
        this.ySize = 183;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    public void drawSlotBG(){
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE);
        for (Slot s:container.inventorySlots)
        {
            drawScaledCustomSizeModalRect(s.xPos-2+offsetX, s.yPos-2+offsetY, 0, 0,80,80,16+4,16+4,80,80);
        }

        for (FluidSlot s:container.fslots)
        {
            drawScaledCustomSizeModalRect(s.x-2+offsetX, s.y-2+offsetY, 0, 0,80,80,16+4,16+4,80,80);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true); //打开键盘连续输入
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawBG();
        drawSlotBG();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        GlStateManager.color(1.0F, 1.0F, 1.0F);
        for(GuiTextField tf:tflist) {
            tf.drawTextBox();
        }

        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();

        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        for(FluidSlot fs:container.fslots) {
            fs.drawSlot(this);
        }

        //绘制tooltip
        for(FluidSlot fs:container.fslots) {
            if(fs.onClick(mouseX-offsetX,mouseY-offsetY))
                fs.drawToolTip(this,mouseX-offsetX,mouseY-offsetY);
        }

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
        int count=0;
        for(FluidSlot fs:container.fslots){
            if(fs.onClick(mouseX-offsetX,mouseY-offsetY)){
                ItemStack stack_put = this.mc.player.inventory.getItemStack();
                IFluidHandler handler=FluidUtil.getFluidHandler(stack_put);
                if(stack_put.isEmpty() || handler==null)
                    continue;

                Utils.interactWithFluidHandler(mc.player, fs instanceof FluidSlotFake?stack_put.copy():stack_put,fs.tank);
                System.out.println("fluid solt");
                /*IItemHandler playerInventory = mc.player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if(handler.drain(1000,false)!=null) {
                    FluidActionResult far=FluidUtil.tryEmptyContainerAndStow(stack_put, fs.tank, playerInventory, Integer.MAX_VALUE, mc.player, true);
                    System.out.println(far.getResult().serializeNBT());
                    mc.player.inventory.setItemStack(far.getResult());
                }else
                    mc.player.inventory.setItemStack(FluidUtil.tryFillContainerAndStow(stack_put,fs.tank,playerInventory,Integer.MAX_VALUE,mc.player,true).getResult());
*/
                NBTTagCompound nbt=new NBTTagCompound();
                nbt.setInteger("index",count);
                if(fs.tank.getFluid()==null){
                    nbt.setTag("fluid", new FluidStack(FluidRegistry.WATER,0).writeToNBT(new NBTTagCompound()));
                } else {
                    nbt.setTag("fluid", fs.tank.getFluid().writeToNBT(new NBTTagCompound()));
                }
                QNetworkManager.INSTANCE.sendPacketToServer(new BasePacket("fluidslot","stack", nbt.toString()));
            }
            count++;
        }

        for(GuiTextField tf:tflist) {
            tf.mouseClicked(mouseX - offsetX, mouseY - offsetY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char par1, int par2) throws IOException {
        for(GuiTextField tf:tflist) {
            if (tf.textboxKeyTyped(par1, par2)) //向文本框传入输入的内容
                return;
        }
        super.keyTyped(par1, par2);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false); //关闭键盘连续输入
    }

    public void addTextField(GuiTextField tf){
        tflist.add(tf);
    }

    public void drawBG(){
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;

        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG);
        drawScaledCustomSizeModalRect(offsetX, offsetY, 0, 0,979,695,xSize,ySize,979,695);
    }

    public void draw_flutank(int x, int y,int mx,int my, FluidTank tank){
        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG_FLU);
        drawScaledCustomSizeModalRect(x-2, y-2, 0, 0,96,216,24,54,96,216);

        //Draw fluid
        Fluid fluid = tank.getFluid()==null?null:tank.getFluid().getFluid();
        if(fluid!=null) {
            TextureAtlasSprite fluidTexture = mc.getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            int flu_h = 50;
            int h = flu_h * tank.getFluidAmount() / tank.getCapacity();
            drawTexturedModalRect(x, y + (flu_h - h), fluidTexture, 20, h);

            //Draw Text
            if(new Rect(x,y,20,50).inRect(mx,my)) {
                List<String> tooltip=new ArrayList<String>();
                tooltip.add(I18n.format(tank.getFluid().getUnlocalizedName()));
                tooltip.add(Utils.getShowNum(tank.getFluidAmount())+"ml/"+Utils.getShowNum(tank.getCapacity())+"ml");
                drawHoveringText(tooltip,mx,my);
            }
                //drawCenteredString(fontRenderer,fluid.getName()+":"+tank.getFluidAmount()+"mb",mx,my,0xffffff);
        }
    }

    public void draw_energy(int x, int y, IQEnergy energy){
        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE_BG_FLU);
        drawScaledCustomSizeModalRect(x-2, y-2, 0, 0,96,216,54,14,96,216);

        //Draw energy bar
        float rate=(float)(energy.getEnergy()/(double)energy.getCapacity());
        mc.renderEngine.bindTexture(GuiElementLoader.TEXTURE_ENERGY_BAR);
        drawScaledCustomSizeModalRect(x, y, 0, 0, (int) (100*rate),20, (int) (50*rate),10,100,20);

        //Draw Text
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(x + 16), (float)(y + 16),0);
        GlStateManager.scale(0.5,0.5,1);
        GlStateManager.translate(-(float)(x + 16), -(float)(y + 16),0);
        drawString(fontRenderer,Utils.getShowNum(energy.getEnergy())+"/"+Utils.getShowNum(energy.getCapacity())+"QE",x,y+18,0xffffff);
        GlStateManager.popMatrix();
    }

    public void drawTexturePoly(Rect drawrect,Rect texrect, int[] vets, float tileWidth, float tileHeight)
    {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        for(int i=0;i<vets.length;i+=2) {
            //System.out.println(texrect.mapto_Y(vets[i + 1], drawrect));
            bufferbuilder.pos(texrect.mapto_X(vets[i], drawrect), texrect.mapto_Y(vets[i + 1], drawrect), 0.0D)
                    .tex(vets[i] * f, vets[i + 1] * f1).endVertex();
        }

        /*bufferbuilder.pos((double)x, (double)(y + height), 0.0D).tex((double)(u * f), (double)((v + (float)vHeight) * f1)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((u + (float)uWidth) * f), (double)((v + (float)vHeight) * f1)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)y, 0.0D).tex((double)((u + (float)uWidth) * f), (double)(v * f1)).endVertex();
        bufferbuilder.pos((double)x, (double)y, 0.0D).tex((double)(u * f), (double)(v * f1)).endVertex();*/
        tessellator.draw();
    }

    public void drawTexturedModalRectWithColor(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn,float[] color)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos((double)(xCoord + 0), (double)(yCoord + heightIn), (double)this.zLevel).tex((double)textureSprite.getMinU(), (double)textureSprite.getMaxV()).color(color[0], color[1], color[2], color[3]).endVertex();
        bufferbuilder.pos((double)(xCoord + widthIn), (double)(yCoord + heightIn), (double)this.zLevel).tex((double)textureSprite.getMaxU(), (double)textureSprite.getMaxV()).color(color[0], color[1], color[2], color[3]).endVertex();
        bufferbuilder.pos((double)(xCoord + widthIn), (double)(yCoord + 0), (double)this.zLevel).tex((double)textureSprite.getMaxU(), (double)textureSprite.getMinV()).color(color[0], color[1], color[2], color[3]).endVertex();
        bufferbuilder.pos((double)(xCoord + 0), (double)(yCoord + 0), (double)this.zLevel).tex((double)textureSprite.getMinU(), (double)textureSprite.getMinV()).color(color[0], color[1], color[2], color[3]).endVertex();
        tessellator.draw();
    }

    public void drawPlayerInv(int x,int y){
        mc.getTextureManager().bindTexture(GuiElementLoader.TEXTURE);
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                drawScaledCustomSizeModalRect(x + j * 18, y + i * 18, 0, 0,80,80,16+4,16+4,80,80);
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            drawScaledCustomSizeModalRect(x + i * 18, y+58, 0, 0,80,80,16+4,16+4,80,80);
        }
    }

    public void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = fontRenderer;
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(x + 16), (float)(y + 16),0);
        GlStateManager.scale(0.5,0.5,1);
        GlStateManager.translate(-(float)(x + 16), -(float)(y + 16),0);
        this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y, altText);
        GlStateManager.popMatrix();

        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
    }

    public void drawFluidStack(FluidStack stack, int x, int y, String text){
        if(stack==null)
            return;
        Fluid fluid=stack.getFluid();
        if(fluid!=null) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableBlend();

            TextureAtlasSprite fluidTexture = mc.getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            float[] color = Utils.color2arr(fluid.getColor(stack));
            drawTexturedModalRectWithColor(x, y , fluidTexture, 16, 16, color);

            //Draw Text
            net.minecraft.client.gui.FontRenderer font = fontRenderer;
            //font.FONT_HEIGHT=5;

            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(x + 16), (float)(y + 16),0);
            GlStateManager.scale(0.5,0.5,1);
            GlStateManager.translate(-(float)(x + 16), -(float)(y + 16),0);

            font.drawStringWithShadow(text, (float)(x + 19 - 2 - font.getStringWidth(text)), (float)(y + 6 + 3), 16777215);

            GlStateManager.popMatrix();

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
        }
    }
}
