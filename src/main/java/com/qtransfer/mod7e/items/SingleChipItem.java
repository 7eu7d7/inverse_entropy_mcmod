package com.qtransfer.mod7e.items;

import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.python.PythonCodeExecutor;
import com.qtransfer.mod7e.python.PythonScript;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SingleChipItem implements INBTSerializable<NBTTagCompound>{

    String folder_name=""+System.currentTimeMillis();
    ItemStack stack;
    String folder_parent="inverse_entropy";
    File folder_this=new File(folder_parent,folder_name);

    boolean init_ok=false;
    public PythonScript script=new PythonScript();

    public static String file_name="main.py"; //TODO 多文件支持

    public SingleChipItem(ItemStack stack){
        this.stack=stack;
        deserializeNBT(stack.getTagCompound());
    }

    public void writeNBT(){
        stack.setTagCompound(serializeNBT());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt=new NBTTagCompound();
        nbt.setString("folder_name",folder_name);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt==null)
            return;
        folder_name=nbt.getString("folder_name");

        folder_this=new File(folder_parent,folder_name);
    }

    public void initScript(){
        new Thread(){
            @Override
            public void run() {
                PythonInterpreter interpreter = PythonScript.createInterpreter();
                try {
                    interpreter.execfile(new File(folder_this,file_name).toString());
                }catch (Throwable e){
                    e.printStackTrace();
                }
                script=new PythonScript(interpreter);
                init_ok=true;
            }
        }.start();
        /*PythonCodeExecutor executor= null;
        try {
            executor = new PythonCodeExecutor(new File(folder_this,file_name));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        new Thread(executor).run();
        script=new PythonScript(executor);*/
    }

    public List<String> getAllScripts(){
        List<String> files = new ArrayList<String>(Arrays.asList(folder_this.list()));
        return files;
    }

    public String readFromFile(String name){
        String res="";
        try {
            res= Utils.readFile(new File(folder_this,name).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void saveToFile(String name, String code){
        if(!folder_this.exists())
            folder_this.mkdirs();
        File tmp=new File(folder_this, file_name);
        if(!tmp.exists()) {
            try {
                tmp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Utils.writeFile(new File(folder_this,name).toString(),code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
