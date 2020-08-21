package com.qtransfer.mod7e.proxy;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface IPacket {
    /**
     * 发包端写数据
     */
    void writeData(ByteBuf out);

    /**
     * 解包端读数据
     */
    void readData(ByteBuf in);
}
