package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.Rect;
import com.qtransfer.mod7e.Utils;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.ArrayList;
import java.util.List;

public class FluidSlot{
    public FluidTank tank;
    int x,y;
    boolean enable=true;

    public FluidSlot(FluidTank tank,int x,int y){
        this.tank=tank;
        this.x=x;
        this.y=y;
    }

    public void drawSlot(GUIBase gui){
        if(enable) {
            //gui.drawFluidStack(new FluidStack(FluidRegistry.LAVA,100), x, y, Utils.getShowNum(tank.getFluidAmount()));
            gui.drawFluidStack(tank.getFluid(), x, y, Utils.getShowNum(tank.getFluidAmount()));
        }
    }

    public void drawToolTip(GUIBase gui,int mousex,int mousey){
        if(tank.getFluid()!=null) {
            List<String> tooltip=new ArrayList<String>();
            tooltip.add(I18n.format(tank.getFluid().getUnlocalizedName()));
            tooltip.add(Utils.getShowNum(tank.getFluidAmount())+"ml/"+Utils.getShowNum(tank.getCapacity())+"ml");
            gui.drawHoveringText(tooltip, mousex, mousey);
        }
    }

    public boolean onClick(int x,int y){
        return enable && new Rect(this.x, this.y, 16, 16).inRect(x, y);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
