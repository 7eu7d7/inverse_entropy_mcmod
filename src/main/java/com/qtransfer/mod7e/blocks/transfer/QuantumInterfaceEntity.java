package com.qtransfer.mod7e.blocks.transfer;

import com.google.common.collect.ArrayListMultimap;
import com.qtransfer.mod7e.Constant;
import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.blocks.QTransTileEntity;
import com.qtransfer.mod7e.items.AdvanceCraftPlugin;
import com.qtransfer.mod7e.items.CraftPluginItem;
import com.qtransfer.mod7e.items.ExtractPluginItem;
import com.qtransfer.mod7e.items.SingleChipItem;
import com.qtransfer.mod7e.python.QInterfacePython;
import com.qtransfer.mod7e.transfer.*;
import com.qtransfer.mod7e.utils.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;

public class QuantumInterfaceEntity extends QTransTileEntity implements IRequestable,ITickable,IQuantumNetwork {

    public String name=System.currentTimeMillis()+"";
    public ItemStackHandler inventory_plugin = new ItemStackHandler(18);
    public List<ItemStack> storage_list;
    public List<ExtractPluginItem> extract_list=new ArrayList<ExtractPluginItem>();
    public List<SingleChipItem> chip_list=new ArrayList<SingleChipItem>();

    ArrayListMultimap<GeneralStack,CraftPluginItem> craft_map=ArrayListMultimap.create();
    Queue<CraftGroup> craft_queue=new LinkedList<CraftGroup>();
    CraftGroup crafting=null;
    IItemHandler craft_handler_item;
    IFluidHandler craft_handler_fluid;

    public WaveStabilizerEntity stabilizer;
    int ext_counter=0;

    @Override
    public void update() {
        if(world.isRemote)
            return;

        //自动合成
        if(crafting==null && !craft_queue.isEmpty()){
            crafting=craft_queue.peek();
            CraftPluginItem cp=craft_map.get(crafting.result).get(0);
            //System.out.println("crafting"+crafting);
            TileEntity entity=world.getTileEntity(pos.offset(cp.face));
            craft_handler_item =entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, cp.face.getOpposite());
            craft_handler_fluid =entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, cp.face.getOpposite());

            if(cp instanceof AdvanceCraftPlugin){ //高级合成模块，有序
                AdvanceCraftPlugin cpa=(AdvanceCraftPlugin)cp;
                int idx_item=0,idx_fluid=0;
                //检查是否能放进机器
                for(GeneralStack stack:cpa.getStuffs_raw()){
                    String toname=stack.fluid ? cpa.fluid_send.get(idx_fluid):cpa.item_send.get(idx_item);
                    if(toname.length()>0){
                        if(!stabilizer.addtoInterface(toname,stack,false).isEmpty()) {
                            //System.out.println("add error"+stack);
                            //高级合成模块，物品送往其他接口
                            crafting = null;
                            return;
                        }
                    } else {
                        if (stack.fluid) {
                            if (!stack.isEmpty() && craft_handler_fluid.fill(stack.fstack, false) < stack.fstack.amount) {
                                crafting = null;
                                return;
                            }
                        } else {
                            if (idx_item >= craft_handler_item.getSlots() || !craft_handler_item.insertItem(idx_item, stack.stack.copy(), true).isEmpty()) {
                                if(!ItemHandlerHelper.insertItemStacked(craft_handler_item, stack.stack.copy(), true).isEmpty()) {
                                    //System.out.println("add item error");
                                    crafting = null;
                                    return;
                                }
                            }
                        }
                    }

                    if (stack.fluid) {
                        idx_fluid++;
                    } else {
                        idx_item++;
                    }
                }

                idx_item=0;idx_fluid=0;
                for(GeneralStack stack:cpa.getStuffs_raw()){
                    String toname=stack.fluid ? cpa.fluid_send.get(idx_fluid):cpa.item_send.get(idx_item);
                    if(toname.length()>0){
                        stabilizer.addtoInterface(toname,stack,true);
                    } else {
                        if (stack.fluid) {
                            if(!stack.isEmpty())
                                craft_handler_fluid.fill(stack.fstack, true);
                        } else {
                            ItemStack stmp=craft_handler_item.insertItem(idx_item, stack.stack.copy(), false);
                            if(!stmp.isEmpty()){ //无法有序合成
                                ItemHandlerHelper.insertItemStacked(craft_handler_item, stmp, false);
                            }
                        }
                    }

                    if (stack.fluid) {
                        idx_fluid++;
                    } else {
                        idx_item++;
                    }
                }
                craft_queue.poll();
            } else { //普通合成模块，无序
                //检查是否能放进机器

                for (GeneralStack stack : crafting.stuff) {
                    if(stack.fluid) {
                        if(craft_handler_fluid.fill(stack.fstack.copy(), false)<stack.fstack.amount) {
                            System.out.println("fill error"+stack.fstack);
                            crafting=null;
                            return;
                        }
                    }else {
                        if(!ItemHandlerHelper.insertItemStacked(craft_handler_item, stack.stack.copy(), true).isEmpty()){
                            System.out.println("fill error"+stack.stack);
                            crafting=null;
                            return;
                        }
                    }
                }
                System.out.println("fill ok");
                for (GeneralStack stack : crafting.stuff) {
                    if(stack.fluid) {
                        craft_handler_fluid.fill(stack.fstack.copy(), true);
                    }else {
                        ItemHandlerHelper.insertItemStacked(craft_handler_item, stack.stack.copy(), false);
                    }
                }
                craft_queue.poll();
            }

            if(entity instanceof ITiggerable)
                ((ITiggerable)entity).tigger();
        }

        if(crafting!=null){
            if (crafting.result.fluid){
                FluidStack fs=crafting.getFluidStack();
                if (craft_handler_fluid.drain(fs,false)!=null && craft_handler_fluid.drain(fs,false).amount >= crafting.getCount()){
                    crafting.result = new GeneralStack(craft_handler_fluid.drain(fs,true));
                    crafting.ctree.onCraftOk(crafting);
                    crafting = null;
                }
            } else {
                ItemStack is=crafting.getItemStack();
                if (Utils.takeItemStack(craft_handler_item, is, true).getCount() >= crafting.getCount()) {
                    crafting.result = new GeneralStack(Utils.takeItemStack(craft_handler_item, is, false));
                    crafting.ctree.onCraftOk(crafting);
                    crafting = null;
                }
            }
        }

        //物品提取
        if(ext_counter>5) {
            ext_counter=0;
            List<IItemHandler> itemHandlers=new LinkedList<IItemHandler>();
            List<IFluidHandler> fluidHandlers=new LinkedList<IFluidHandler>();
            for(EnumFacing face:EnumFacing.VALUES){
                TileEntity entity;
                if((entity=world.getTileEntity(pos.offset(face)))!=null){
                    if(entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,face.getOpposite())){
                        itemHandlers.add(entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,face.getOpposite()));
                    }
                    if(entity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,face.getOpposite())){
                        fluidHandlers.add(entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,face.getOpposite()));
                    }
                }
            }

            for (ExtractPluginItem epi : extract_list) {
                HashMap<GeneralStack,Integer> stackmap=new HashMap<GeneralStack,Integer>();

                for(IItemHandler handler:itemHandlers){ //遍历相邻的储存器
                    for(int i=0;i<epi.inventory_item.getSlots();i++) { //遍历要提取的物品
                        ItemStack stack_take=epi.inventory_item.getStackInSlot(i);
                        if(!stack_take.isEmpty()) {
                            GeneralStack stack = new GeneralStack(Utils.takeItemStack(handler, stack_take, false));
                            if(!stack.isEmpty()) {
                                if (!stackmap.containsKey(stack))
                                    stackmap.put(stack, 0);
                                stackmap.put(stack, stackmap.get(stack) + stack.getCount());
                            }
                        }
                    }
                }

                for(IFluidHandler handler:fluidHandlers){
                    for(int i=0;i<epi.inventory_fluid.size();i++) { //遍历要提取的物品
                        FluidStack stack_take=epi.inventory_fluid.get(i).getFluid();
                        if(stack_take!=null && stack_take.amount>0) {
                            GeneralStack stack = new GeneralStack(handler.drain(stack_take, true));
                            if(!stack.isEmpty()) {
                                if (!stackmap.containsKey(stack))
                                    stackmap.put(stack, 0);
                                stackmap.put(stack, stackmap.get(stack) + stack.getCount());
                            }
                        }
                    }
                }

                //送至目标接口
                if(stabilizer.qi_list.containsKey(epi.target)) {
                    stackmap=stabilizer.qi_list.get(epi.target).onStackArrive(stackmap);
                    if(stackmap.size()>0) {
                        for (Map.Entry<GeneralStack, Integer> entry : stackmap.entrySet()) {
                            GeneralStack stack = entry.getKey().copy();
                            stack.setCount(entry.getValue());
                            stabilizer.addtoStorage(stack);
                        }
                    }
                }
            }
        }
        ext_counter++;

        //单片机
        for(SingleChipItem chip:chip_list){
            chip.script.callFunction("tick",new QInterfacePython(this));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.inventory_plugin.deserializeNBT(tag.getCompoundTag("Inventory_plugin"));
        name=tag.getString("qname");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("Inventory_plugin", this.inventory_plugin.serializeNBT());
        tag.setString("qname",name);
        super.writeToNBT(tag);
        return tag;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<GeneralStack> getStuffs(GeneralStack result) {
        List<GeneralStack> stuff=craft_map.get(result).get(0).getStuffs();
        return stuff;
    }

    @Override
    public List<GeneralStack> getCraftList() {//获取可合成的列表
        return new ArrayList<GeneralStack>(craft_map.keySet());
    }

    @Override
    public int getCraftingCount() {
        return craft_queue.size();
    }

    @Override
    public void addtoCraft(CraftGroup gc) {
        craft_queue.offer(gc);
    }

    @Override
    public void generageStorageMap(ArrayListMultimap<GeneralStack, IHandlerSolt> total_map) {
        for(ItemStack stack:storage_list){
            NBTTagCompound nbt=stack.getTagCompound();
            EnumFacing face=EnumFacing.VALUES[nbt.getInteger("face")];
            TileEntity entity=world.getTileEntity(getPos().offset(face));
            if(entity!=null) {
                System.out.println(entity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite()));
                if(entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite())) {
                    IItemHandler handler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
                    for (int i = 0; i < handler.getSlots(); i++) {
                        if (!handler.getStackInSlot(i).isEmpty()) {
                            total_map.put(new GeneralStack(handler.getStackInSlot(i)), new ItemHandlerSlot(handler, i));
                        }
                    }
                }
                if(entity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite())){
                    IFluidHandler handler=entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite());
                    IFluidTankProperties[] props=handler.getTankProperties();
                    for(int i=0;i<props.length;i++){
                        if (props[i].canDrain() && props[i].getContents()!=null) {
                            total_map.put(new GeneralStack(props[i].getContents()), new FluidHandlerSlot(handler, i));
                        }
                    }
                }
            }
        }
    }

    public List<ItemStack> getPlugins(String type){
        Item item=Item.getByNameOrId(Constant.item(type));
        List<ItemStack> plugins=new ArrayList<ItemStack>();
        for(int i=0;i<inventory_plugin.getSlots();i++){
            if(inventory_plugin.getStackInSlot(i).getItem()==item){
                plugins.add(inventory_plugin.getStackInSlot(i));
            }
        }
        return plugins;
    }

    @Override
    public void updateNetwork(){
        storage_list=getPlugins("storage_plugin");

        //合成插件
        EnumFacing craft_face=null;
        for(EnumFacing face:EnumFacing.VALUES){
            TileEntity entity=world.getTileEntity(pos.offset(face));
            if(entity!=null && (entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()) ||
                            entity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite()))) {
                craft_face=face;
                break;
            }
        }
        if(craft_face!=null) {
            craft_map.clear();
            List<ItemStack> craft_plugins = getPlugins("craft_plugin");
            for (ItemStack stack : craft_plugins) {
                CraftPluginItem cp = new CraftPluginItem(stack);
                cp.setFace(craft_face);
                craft_map.put(cp.getResult(), cp);
            }
            List<ItemStack> adv_craft_plugins = getPlugins("adv_craft_plugin");
            for (ItemStack stack : adv_craft_plugins) {
                AdvanceCraftPlugin cp = new AdvanceCraftPlugin(stack);
                cp.setFace(craft_face);
                craft_map.put(cp.getResult(), cp);
            }
        }

        //提取插件
        extract_list.clear();
        List<ItemStack> extract_plug_list=getPlugins("extract_plugin");
        for(ItemStack stack:extract_plug_list){
            extract_list.add(new ExtractPluginItem(stack));
        }

        //单片机
        for(SingleChipItem chi:chip_list) chi.stopRunning(); //停止原有线程

        chip_list.clear();
        List<ItemStack> single_chip_list=getPlugins("single_chip");
        for(ItemStack stack:single_chip_list){
            SingleChipItem chip=new SingleChipItem(stack);
            chip.initScript();
            chip_list.add(chip);
        }
    }

    @Override
    public GeneralStack addItem(GeneralStack items, boolean doadd) { //往储存器里添加物品
        for(ItemStack stack:storage_list){
            NBTTagCompound nbt=stack.getTagCompound();
            EnumFacing facing=EnumFacing.byName(nbt.getString("face"));
            TileEntity tile=world.getTileEntity(pos.offset(facing));
            if(tile!=null) {
                if (items.fluid) {
                    IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
                    items = items.copy(items.getCount() - handler.fill(items.fstack, doadd));
                } else {
                    IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
                    items = new GeneralStack(ItemHandlerHelper.insertItem(handler, items.stack, !doadd));
                    if (items.isEmpty())
                        return items;
                }
            }
        }
        return items;
    }

    @Override
    public GeneralStack addItemAny(GeneralStack items, boolean doadd) {
        for(EnumFacing face:EnumFacing.VALUES){
            TileEntity tile=world.getTileEntity(pos.offset(face));
            if(tile!=null) {
                if (items.fluid) {
                    IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite());
                    if (handler != null)
                        items = items.copy(items.getCount() - handler.fill(items.fstack, doadd));
                } else {
                    IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
                    if (handler != null) {
                        items = new GeneralStack(ItemHandlerHelper.insertItem(handler, items.stack, !doadd));
                        if (items.isEmpty())
                            return items;
                    }
                }
            }
        }
        return items;
    }

    @Override
    public boolean hasPlugin(String name) {
        return getPlugins(name).size()>0;
    }

    @Override
    public void setWaveStabilizer(WaveStabilizerEntity stabilizer) {
        this.stabilizer=stabilizer;
    }

    @Override
    public HashMap<GeneralStack,Integer> onStackArrive(HashMap<GeneralStack,Integer> stackmap) {
        for(EnumFacing face:EnumFacing.VALUES){
            TileEntity entity;
            if((entity=world.getTileEntity(pos.offset(face)))!=null){
                if(entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,face.getOpposite())){
                    IItemHandler handler=entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,face.getOpposite());
                    for(Map.Entry<GeneralStack,Integer> entry:stackmap.entrySet()){
                        GeneralStack stack=entry.getKey().copy();
                        if(!stack.fluid) {
                            stack.setCount(entry.getValue());
                            stackmap.put(stack,ItemHandlerHelper.insertItemStacked(handler, stack.stack, false).getCount());

                            if(stackmap.get(stack)<=0)
                                stackmap.remove(stack);
                        }
                    }
                }
                if(entity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,face.getOpposite())){
                    IFluidHandler handler=entity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,face.getOpposite());
                    for(Map.Entry<GeneralStack,Integer> entry:stackmap.entrySet()){
                        GeneralStack stack=entry.getKey().copy();
                        if(stack.fluid) {
                            stack.setCount(entry.getValue());
                            stackmap.put(stack,entry.getValue()-handler.fill(stack.fstack,true));

                            if(stackmap.get(stack)<=0)
                                stackmap.remove(stack);
                        }
                    }
                }
            }
        }
        return stackmap;
    }

    @Override
    public Object onObjectArrive(Object obj) {
        //单片机
        for(SingleChipItem chip:chip_list){
            chip.script.callFunction("obj_arrive",new QInterfacePython(this),obj);
            /*PyFunction func=null;
            if((func=chip.script.getFunction("obj_arrive"))!=null){
                return func.__call__(Py.javas2pys(new TileEntityPython(this),obj));
            }*/
        }
        return null;
    }

}
