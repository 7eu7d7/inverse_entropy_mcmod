package com.qtransfer.mod7e.blocks.transfer;

import com.google.common.collect.ArrayListMultimap;
import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.blocks.QTransBlock;
import com.qtransfer.mod7e.blocks.QTransTileEntity;
import com.qtransfer.mod7e.transfer.*;
import com.qtransfer.mod7e.utils.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;

public class WaveStabilizerEntity extends QTransTileEntity implements ICraftTree,IQuantumNetwork,ITickable {

    public HashMap<String,IRequestable> qi_list=new HashMap<String,IRequestable>(); //name, interface
    public List<IStorageable> buffer_list=new ArrayList<IStorageable>();
    public ArrayListMultimap<GeneralStack,IRequestable> req_map=ArrayListMultimap.create();
    public ArrayListMultimap<GeneralStack,IHandlerSolt> storage_map=ArrayListMultimap.create();

    private int craft_id=1;
    private long cft_uid=0;
    private boolean docraft=false;
    private HashMap<Long, HashMap<Integer,CTreeItem>> ctrees=new HashMap<Long, HashMap<Integer,CTreeItem>>();
    private HashMap<GeneralStack,Integer> avl_stuff=new HashMap<GeneralStack,Integer>();

    public ItemStackHandler inventory = new ItemStackHandler(27);
    public FluidTankList fluid_inventory = new FluidTankList(9);

    boolean initok=false;


    @Override
    public void update() {
        if(world.isRemote)
            return;

        if(!initok){
            QTransBlock.updateQuantumNet(world,pos);
            initok=true;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        fluid_inventory.deserializeNBT(compound.getCompoundTag("inventory_fluid"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory",inventory.serializeNBT());
        compound.setTag("inventory_fluid",fluid_inventory.serializeNBT());
        super.writeToNBT(compound);
        return compound;
    }

    @Override
    public HashMap<Integer,CTreeItem> generateTree(GeneralStack item_request) {
        HashMap<Integer,CTreeItem> ctree=new HashMap<Integer,CTreeItem>();
        cft_uid = System.currentTimeMillis();
        ctrees.put(cft_uid, ctree);

        craft_id=0;
        ctree.clear();
        avl_stuff=availableStuff();

        CTreeItem cti0=new CTreeItem(-1);
        cti0.residue = item_request.getCount();
        cti0.result=item_request.copy();
        cti0.result.setCount(1);
        ctree.put(0,cti0);

        GeneralStack ic=item_request.copy();
        ic.setCount(1);
        makeTreeNode(ic,0);

        /*for(GeneralStack ish:storage_map.keySet()){
            System.out.println(ish.toString());
        }*/

        /*for(GeneralStack ish:avl_stuff.keySet()){
            System.out.println(ish.toString()+","+avl_stuff.get(ish));
        }*/

        for(Map.Entry<Integer, CTreeItem> entry : ctree.entrySet())
            System.out.println(entry.getKey()+","+entry.getValue());
        return ctree;
    }

    public void makeTreeNode(GeneralStack result, int nextid){
        HashMap<Integer,CTreeItem> ctree=ctrees.get(cft_uid);

        boolean noend=true;
        craft_id++;

        CTreeItem nextc=ctree.get(nextid);
        nextc.subcraft.put(craft_id,0);
        nextc.subcraft_need.put(craft_id,result.getCount());//单份合成需求量

        CTreeItem cti=new CTreeItem(nextid);
        cti.thisid=craft_id;
        cti.result=result.copy(); //单份产物

        GeneralStack tmp=Utils.getKey(req_map.keySet(),result);
        cti.per_result = tmp==null?1:tmp.getCount();
        cti.residue = result.getCount()*nextc.residue;
        int residue_raw=cti.residue; //实际所需物品个数
        cti.residue = cti.getNeedStuff();  //适配合成产物有多个的情况
        GeneralStack ish=result.copy();

        if(!avl_stuff.containsKey(ish))
            avl_stuff.put(ish,0);
        if(!req_map.containsKey(ish)){//叶子节点(无子合成)
            avl_stuff.put(ish,avl_stuff.get(ish)-cti.residue);
            nextc.subcraft.put(craft_id,cti.residue);
            noend=false;
        }else if(avl_stuff.get(ish)>0){
            if(avl_stuff.get(ish)>=residue_raw) {//叶子节点(无需合成)
                nextc.subcraft.put(craft_id,residue_raw);
                avl_stuff.put(ish,avl_stuff.get(ish)-residue_raw);
                noend=false;
            }else{//部分合成
                nextc.subcraft.put(craft_id, avl_stuff.get(ish));
                cti.residue=residue_raw-avl_stuff.get(ish);
                cti.residue = cti.getNeedStuff();  //适配合成产物有多个的情况
                avl_stuff.put(ish,0);
            }
        }
        ctree.put(craft_id,cti);
        if(noend) {
            List<GeneralStack> stuffs = req_map.get(ish).get(0).getStuffs(ish);
            int sub_id=craft_id;
            for (GeneralStack sts : stuffs) {
                makeTreeNode(sts, sub_id);
            }
        }
    }

    @Override
    public void checkStuff() throws Exception{
        HashMap<GeneralStack,Integer> lack=new HashMap<GeneralStack,Integer>();
        for (Map.Entry<GeneralStack, Integer> entry : avl_stuff.entrySet()) {
            if(entry.getValue()<0){
                lack.put(entry.getKey(),-entry.getValue());
            }
        }

        if(lack.size()>0) {
            String err="";
            for(Map.Entry<GeneralStack, Integer> entry:lack.entrySet()){
                err+=("缺少："+entry.getKey().getName()+" x"+entry.getValue());
            }
            throw new Exception(err);
        }

        //判断缓存器是否足够
        int need=0;
        boolean fitst=true;
        for(CTreeItem cti: ctrees.get(cft_uid).values()){
            if(fitst){
                fitst=false;
                continue;
            }

            need+=cti.residue;
            for(int i:cti.subcraft.values())
                need+=i;
        }
        int max_storage=0;
        for(IStorageable buff:buffer_list)
            max_storage+=buff.getMaxSize();
        if(need>max_storage)
            throw new Exception("缓存器空间不足");
    }

    @Override
    public void stratCraft() {
        HashMap<Integer,CTreeItem> ctree=ctrees.get(cft_uid);

        for (Map.Entry<Integer,CTreeItem> entry : ctree.entrySet()) {
            CTreeItem cti=entry.getValue();
            for(Map.Entry<Integer,Integer> entry_c : cti.subcraft.entrySet()){
                if(entry_c.getValue()>0){
                    //从箱子里取出物品加入缓存器
                    addtoBuffer(takefromStorage(ctree.get(entry_c.getKey()).result,entry_c.getValue()));
                }
            }
            while (cti.canCraft()){
                //onCraftOk(packCtree(cti));
                if(cti.thisid==0)
                    onCraftOk(packCtree(cti,cft_uid));
                else
                    sendtoRequester(packCtree(cti,cft_uid));
            }
        }
        docraft=true;
    }

    @Override
    public void onCraftOk(CraftGroup cg) {
        HashMap<Integer,CTreeItem> ctree=ctrees.get(cg.uid);

        CTreeItem cthis=ctree.get(cg.craftid);
        //System.out.println(cthis.thisid+","+cg);
        if(cthis.thisid==1 || cthis.thisid==0){
            if(cg.result.fluid){
                GeneralStack rest = Utils.insertItemHash(fluid_inventory, cg.result.copy(), false);
                /*if (!rest.isEmpty())
                    Utils.dropItemHash(world, pos, rest);*/
                //同步至客户端
                syncToTrackingClients();
                //markDirty();
            } else {
                GeneralStack rest = Utils.insertItemHash(inventory, cg.result.copy(), false);
                if (!rest.isEmpty())
                    Utils.dropItemHash(world, pos, rest);
            }
            if(cthis.crafted_count>=cthis.residue){
                System.out.println("craft ok");
            }
            return;
        }
        CTreeItem next=ctree.get(cthis.nextid);
        next.subcraft.put(cg.craftid,next.subcraft.get(cg.craftid)+cg.result.getCount());
        addtoBuffer(cg.result);
        while (next.canCraft()) {
            sendtoRequester(packCtree(next,cg.uid));
        }
    }

    public CraftGroup packCtree(CTreeItem cti,long uid){
        HashMap<Integer,CTreeItem> ctree=ctrees.get(uid);

        cti.oneCraft();
        CraftGroup cg=new CraftGroup();
        cg.craftid=cti.thisid;
        cg.ctree=this;
        cg.uid=uid;

        cg.stuff=new ArrayList<GeneralStack>();
        for(Map.Entry<Integer,Integer> entry : cti.subcraft_need.entrySet()){
            cg.stuff.add(takefromBuffer(ctree.get(entry.getKey()).result,entry.getValue()));
        }
        cg.result=cti.result;
        cg.per_result=cti.per_result;
        return cg;
    }

    public void sendtoRequester(CraftGroup cg){
        IRequestable irq_min=null;
        int min=Integer.MAX_VALUE;
        for(IRequestable irq:req_map.get(cg.result)){
            if(irq.getCraftingCount()<min){
                min=irq.getCraftingCount();
                irq_min=irq;
            }
        }

        irq_min.addtoCraft(cg);
        System.out.println("to requ "+cg.toString());
    }

    //----------------buffer--------------------
    public void addtoBuffer(GeneralStack item){
        for(IStorageable iqb:buffer_list){
            if(item.isEmpty())
                break;
            item=iqb.addItem(item,true);
        }
        if(!item.isEmpty())
            Utils.dropItemHash(world,pos,item);
    }

    public GeneralStack takefromBuffer(GeneralStack item, int amount){
        GeneralStack res=item.getEmpty();
        for(IStorageable iqb:buffer_list){
            if(res.isEmpty()) {
                res = iqb.takeItem(item,amount, true);
                amount-=res.getCount();
            } else {
                GeneralStack tmp=iqb.takeItem(item,amount, true);
                res.grow(tmp.getCount());
                amount-=tmp.getCount();
            }

            if(amount==0)
                break;
        }
        return res;
    }
    //-------------------------------------------

    //----------------storage--------------------
    public GeneralStack addtoStorage(GeneralStack item){
        for(IRequestable irq:qi_list.values()){
            if(((QuantumInterfaceEntity)irq).storage_list.size()>0) {
                if (item.isEmpty())
                    break;
                item = irq.addItem(item, true);
            }
        }
        return item;
    }

    public GeneralStack takefromStorage(GeneralStack item, int amount){
        List<IHandlerSolt> ihslist=storage_map.get(item);
        GeneralStack res=item.getEmpty();
        for(IHandlerSolt ihs:ihslist){
            if(res.isEmpty()) {
                res = ihs.extract(amount);
                amount-=res.getCount();
            } else {
                GeneralStack tmp=ihs.extract(amount);
                res.grow(tmp.getCount());
                amount-=tmp.getCount();
            }

            if(amount==0)
                break;
        }
        return res;
    }
    //-------------------------------------------

    public GeneralStack addtoInterface(String name,GeneralStack item,boolean doadd){
        if(qi_list.containsKey(name)){
           return qi_list.get(name).addItemAny(item,doadd);
        }
        return item;
    }

    public HashMap<GeneralStack,Integer> availableStuff(){
        HashMap<GeneralStack,Integer> stuffs=new HashMap<GeneralStack,Integer>();
        for (Map.Entry<GeneralStack, Collection<IHandlerSolt>> entry : storage_map.asMap().entrySet()) {
            int count=0;
            for(IHandlerSolt ihs:entry.getValue())
                count+=ihs.getCount();
            stuffs.put(entry.getKey(),count);
        }
        return stuffs;
    }

    @Override
    public void updateNetwork(){
        req_map.clear();
        storage_map.clear();

        for(IRequestable irq:qi_list.values()){
            irq.setWaveStabilizer(this);
            irq.updateNetwork();

            //更新合成插件列表
            List<GeneralStack> craft_list=irq.getCraftList();
            for(GeneralStack i:craft_list){
                req_map.put(i,irq);
            }

            //更新储存插件列表
            irq.generageStorageMap(storage_map);
        }

        for(IStorageable ist:buffer_list){
            ((QuantumBufferEntity)ist).updateNetwork();
        }
    }
}
