package com.qtransfer.mod7e;

import com.google.common.base.Preconditions;
import com.qtransfer.mod7e.transfer.GeneralStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.FileSystems;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utils {

    public static String codeConvert(String str,String code){
        try {
            return new String( str.getBytes(code) , code);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static <T>T[] catArray(T[]... arr){
        List<T> list=new ArrayList<T>();
        for(T[] item:arr){
            Collections.addAll(list, item);
        }
        return list.toArray(arr[0]);
    }

    public static String readAssets(String path){
        try {
            return readStr(Utils.class.getClassLoader().getResourceAsStream("assets/qtrans/"+path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getResourceListing(String spath) throws URISyntaxException, IOException {
        URI uri = Utils.class.getClassLoader().getResource("assets/qtrans/"+spath).toURI();
        Map<String, String> env = new HashMap<>();
        List<String> flist=new ArrayList<String>();
        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            for (Path path : zipfs.getRootDirectories()) {
                Files.list(path.resolve("assets/qtrans/"+spath))
                        .forEach(p -> flist.add(p.toString()));
            }
        }
        System.out.println("flen:"+flist.size());
        return flist;
    }

    public static List<String> getResourceListing(String spath, String ends) throws URISyntaxException, IOException {
        URI uri = Utils.class.getClassLoader().getResource("assets/qtrans/"+spath).toURI();
        Map<String, String> env = new HashMap<>();
        List<String> flist=new ArrayList<String>();
        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            for (Path path : zipfs.getRootDirectories()) {
                Files.list(path.resolve("assets/qtrans/"+spath))
                        .forEach(p -> {
                            if(p.toString().endsWith(ends))
                                flist.add(p.toString());
                        });
            }
        }
        System.out.println("flen:"+flist.size());
        return flist;
    }

    public static String readStr(InputStream is) throws IOException {
        byte[] bys=new byte[is.available()];
        is.read(bys);
        is.close();
        return new String(bys);
    }

    public static String readFile(String file_name) throws IOException {
        return readStr(new FileInputStream(file_name));
    }

    public static void writeFile(String name,String data) throws IOException{
        FileOutputStream fout=new FileOutputStream(name);
        fout.write(data.getBytes());
        fout.flush();
        fout.close();
    }

    public static Vec3d interpolate(Entity entity, float v) {
        return new Vec3d(
                entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * v,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * v,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * v
        );
    }

    public static void drawLine(BufferBuilder worldrenderer, Tessellator tessellator, double x1, double y1, double z1, double x2, double y2, double z2) {
        worldrenderer.pos(x1, y1, z1).endVertex();
        worldrenderer.pos(x2, y2, z2).endVertex();
    }

    public static void dropItemStackAsEntity(World world, BlockPos pos, @Nonnull ItemStack toDrop) {
        // 掉落物是实体，所以首先要先有一个实体
        EntityItem entityItem = new EntityItem(world, pos.getX()+1, pos.getY(), pos.getZ(), toDrop);
        // 速度向量，nextGaussian 返回一正态分布的伪随机双精浮点数，范围 [0.0, 1.0)
        // * 0.05D 避免物品直接嗖一下飞出去。Y 方向多 0.2D 保证它先往上飞一会然后再掉下去。
        entityItem.motionX = Math.random() * 0.05D;
        entityItem.motionY = Math.random() * 0.05D + 0.2D;
        entityItem.motionZ = Math.random() * 0.05D;
        // 最终生成实体
        world.spawnEntity(entityItem);
    }

    public static ItemStack takeItemStack(IItemHandler handler, ItemStack item, boolean simulate){
        int amount=item.getCount();
        ItemStack res=ItemStack.EMPTY;
        for(int i=0;i<handler.getSlots();i++){
            ItemStack tmp=null;
            if(GeneralStack.hashCode(handler.getStackInSlot(i))== GeneralStack.hashCode(item)) {
                tmp = handler.extractItem(i, amount, simulate);
            } else if(handler.getStackInSlot(i).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,null)){
                ItemStack sub_item=item.copy();
                sub_item.setCount(amount);
                tmp=takeItemStack(handler.getStackInSlot(i).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,null), sub_item, simulate);
            }

            if(tmp!=null){
                if(res.isEmpty()) res=tmp;
                else res.grow(tmp.getCount());
                amount -= tmp.getCount();
            }

            if(amount==0)
                break;
        }
        return res;
    }

    public static void dropItemHash(World world, BlockPos pos, @Nonnull GeneralStack toDrop){
        dropItemStackAsEntity(world,pos,toDrop.stack);
    }

    public static GeneralStack insertItemHash(IItemHandler handler, GeneralStack stack, boolean simulate){
        if(stack.fluid)
            return stack;
        else
            return new GeneralStack(ItemHandlerHelper.insertItemStacked(handler,stack.stack,false));
    }

    public static GeneralStack insertItemHash(IFluidHandler handler, GeneralStack stack, boolean simulate){
        if(!stack.fluid)
            return stack;
        else {
            FluidStack tmp=stack.fstack.copy();
            tmp.amount=handler.fill(stack.fstack, true);
            return new GeneralStack(tmp);
        }
    }

    public static String getShowNum(long count){
        String[] unit={"","K","M","G","T","P"};

        double tmp=1e3;
        for(int i=0;i<unit.length;i++){
            if(count<tmp || i==unit.length-1)
                return String.format("%.1f", (float)(count/(tmp/1e3)))+unit[i];
            tmp*=1e3;
        }
        return count+"";
    }

    public static boolean interactWithFluidHandler(@Nonnull EntityPlayer player, @Nonnull ItemStack heldItem, @Nonnull IFluidHandler handler)
    {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(heldItem);
        Preconditions.checkNotNull(handler);

        if (!heldItem.isEmpty())
        {
            IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (playerInventory != null)
            {
                FluidActionResult fluidActionResult = FluidUtil.tryFillContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                System.out.println("fill "+fluidActionResult.isSuccess());
                if (!fluidActionResult.isSuccess())
                {
                    fluidActionResult = FluidUtil.tryEmptyContainerAndStow(heldItem, handler, playerInventory, Integer.MAX_VALUE, player, true);
                }

                System.out.println("empty "+fluidActionResult.getResult());
                if (fluidActionResult.isSuccess())
                {
                    player.inventory.setItemStack(fluidActionResult.getResult());
                    //player.setHeldItem(hand, fluidActionResult.getResult());
                    return true;
                }
            }
        }
        return false;
    }

    public static FluidStack fluidFill(IFluidHandler handler,FluidStack stack){
        int sc=stack.amount;
        int filled=handler.fill(stack,true);
        FluidStack res=stack.copy();
        res.amount=sc-filled;
        return res;
    }

    public static String getJarPath(Class cls) {
        java.net.URL url = cls.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        filePath = filePath.substring(filePath.indexOf("file:")+6,filePath.indexOf(".jar")+4);
        return filePath;
    }

    public static <T> String list2str(List<T> list){
        StringBuilder str=new StringBuilder();
        for(T t:list){
            str.append(t).append(";");
        }
        return str.toString();
    }

    public static float[] color2arr(int col){
        float fAlpha = (float)(col >> 24) / 0xFF;
        float fRed = (float)((col >> 16) & 0xFF) / 0xFF;
        float fGreen = (float)((col >> 8) & 0xFF) / 0xFF;
        float fBlue = (float)(col & 0xFF) / 0xFF;
        return new float[]{fRed,fGreen,fBlue,fAlpha};
    }

    public static String[] getOreNames(ItemStack stack){
        int[] ids = OreDictionary.getOreIDs(stack);
        String[] list = new String[ids.length];
        for(int i=0;i<ids.length;i++){
            list[i] = OreDictionary.getOreName(ids[i]);
        }
        return list;
    }

    public static boolean contain_str(String[] strs,String target){
        for(String s:strs)
            if(s.equals(target))
                return true;
        return false;
    }

    public static <T>T getKey(Set<T> set, T key){
        for(T item:set){
            if(key.equals(item))
                return item;
        }
        return null;
    }

}
