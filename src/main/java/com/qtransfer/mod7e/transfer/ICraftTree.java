package com.qtransfer.mod7e.transfer;

import java.util.HashMap;

public interface ICraftTree {
    HashMap<Integer,CTreeItem> generateTree(GeneralStack item_request);
    void checkStuff() throws Exception;
    void stratCraft();
    void onCraftOk(CraftGroup cg);
}
