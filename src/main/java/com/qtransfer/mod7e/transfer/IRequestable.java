package com.qtransfer.mod7e.transfer;

import com.google.common.collect.ArrayListMultimap;
import com.qtransfer.mod7e.blocks.transfer.WaveStabilizerEntity;
import com.qtransfer.mod7e.utils.IHandlerSolt;

import java.util.HashMap;
import java.util.List;

public interface IRequestable extends IQuantumNetwork{
    String getName();
    List<GeneralStack> getStuffs(GeneralStack result);
    List<GeneralStack> getCraftList();
    int getCraftingCount(); //正在合成的任务
    void addtoCraft(CraftGroup gc);
    void generageStorageMap(ArrayListMultimap<GeneralStack,IHandlerSolt> total_map); //生成储存模块对应箱子ItemStack的Map
    GeneralStack addItem(GeneralStack items, boolean doadd);
    GeneralStack addItemAny(GeneralStack items, boolean doadd);

    boolean hasPlugin(String name);

    void setWaveStabilizer(WaveStabilizerEntity stabilizer);
    HashMap<GeneralStack,Integer> onStackArrive(HashMap<GeneralStack,Integer> stack);

    Object onObjectArrive(Object obj);
}
