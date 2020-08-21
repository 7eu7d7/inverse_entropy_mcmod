package com.qtransfer.mod7e.transfer;

import java.util.HashMap;
import java.util.Map;

public class CTreeItem {
    //预处理
    public int nextid=0; //0完毕
    public int thisid;
    public int per_result=1;
    public HashMap<Integer,Integer> subcraft=new HashMap<Integer,Integer>();
    public HashMap<Integer,Integer> subcraft_need=new HashMap<Integer,Integer>(); //craftid,count
    public int residue; //剩余
    public GeneralStack result; //单份产物

    //合成中
    public int crafted_count;  //记录合成时已合成数量

    public CTreeItem(int nextid){
        this.nextid=nextid;
    }

    public boolean canCraft(){
        if(subcraft.size()<=0)
            return false;
        for(Map.Entry<Integer,Integer> entry : subcraft_need.entrySet()){
            if(subcraft.get(entry.getKey())<entry.getValue())
                return false;
        }
        return true;
    }

    public void oneCraft(){
        for(Map.Entry<Integer,Integer> entry : subcraft_need.entrySet()){
            subcraft.put(entry.getKey(),subcraft.get(entry.getKey())-entry.getValue());
        }
        crafted_count+=per_result;
    }

    public int getNeedStuff(){
        return (int)Math.ceil((double) residue/per_result);
    }

    @Override
    public String toString() {
        String res="id:"+thisid+",";
        res+="result:"+result+",";
        res+="next:"+nextid+",";
        res+="residue:"+residue+",";
        res+="need[";
        for(int i:subcraft_need.keySet())
            res+="id:"+i+",";
        res+="],";
        res+="sub[";
        for(int i:subcraft.values())
            res+="have:"+i+",";
        res+="]";
        return res;
    }
}
