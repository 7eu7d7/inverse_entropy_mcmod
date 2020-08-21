package com.qtransfer.mod7e.gui.widget;

import net.minecraft.client.gui.Gui;

public class ListView extends Gui {
    private final int id;
    public int x;
    public int y;
    /** The width of this text field. */
    public int width;
    public int height;

    public ListView(int componentId, int x, int y, int width, int height)
    {
        this.id = componentId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(){

    }


}
