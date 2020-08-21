package com.qtransfer.mod7e.proxy;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BasePacket implements IPacket{
    public String group,type,data;
    public final Charset utf8=Charset.forName("utf8");

    public BasePacket(String group, String type,String data){
        this.group=group;
        this.type=type;
        this.data=data;
    }

    public BasePacket(ByteBuf buf){
        readData(buf);
    }

    @Override
    public void writeData(ByteBuf out) {
        out.writeInt(group.length());
        out.writeCharSequence(group, utf8);
        out.writeInt(type.length());
        out.writeCharSequence(type, utf8);
        out.writeInt(data.length());
        out.writeCharSequence(data, utf8);
    }

    @Override
    public void readData(ByteBuf in) {
        group=(String) in.readCharSequence(in.readInt(),utf8);
        type=(String) in.readCharSequence(in.readInt(),utf8);
        data=(String) in.readCharSequence(in.readInt(),utf8);
    }

}
