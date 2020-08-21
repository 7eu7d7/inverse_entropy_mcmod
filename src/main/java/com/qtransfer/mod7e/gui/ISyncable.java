package com.qtransfer.mod7e.gui;

import com.qtransfer.mod7e.proxy.BasePacket;

public interface ISyncable {
    void dataRecv(BasePacket packet);
}
