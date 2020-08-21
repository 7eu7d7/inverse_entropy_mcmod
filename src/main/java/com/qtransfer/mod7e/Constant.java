package com.qtransfer.mod7e;

import net.minecraft.util.ResourceLocation;

public class Constant {
    public static final String MODID = "qtrans";
    public static final String NAME = "Inverse Entropy";
    public static final String VERSION = "0.2";

    public static final String MINECRAFT_LIBS = "libraries/";

    public static final ResourceLocation TEXTURE_LIGHTING=new ResourceLocation(MODID,"textures/effect/lighting.png");

    public static String item(String name){
        return MODID+":"+name;
    }
}
