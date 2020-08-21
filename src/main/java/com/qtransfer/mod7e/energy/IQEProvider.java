package com.qtransfer.mod7e.energy;

import java.util.List;

public interface IQEProvider extends IQEnergy{
    void setUsers(List<IQEUser> users);

    void sendEnergy();
}
