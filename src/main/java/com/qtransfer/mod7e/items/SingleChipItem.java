package com.qtransfer.mod7e.items;

import com.qtransfer.mod7e.Utils;
import com.qtransfer.mod7e.python.PythonScript;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SingleChipItem implements INBTSerializable<NBTTagCompound>{

    String folder_name=""+System.currentTimeMillis();
    ItemStack stack;
    String folder_parent="./inverse_entropy";
    File folder_this=new File(folder_parent,folder_name);

    volatile boolean init_ok=false;
    //public volatile PythonScript script=new PythonScript();

    public String file_name="main.py"; //当前打开的文件

    public final String file_run_txt="start.txt"; //标记运行主文件
    public String file_run="main.py"; //运行主文件

    public volatile PythonScript script=new PythonScript();

    public SingleChipItem(ItemStack stack){
        this.stack=stack;
        deserializeNBT(stack.getTagCompound());
        check_init();
    }

    public void check_init(){
        //System.out.println(stack+";"+folder_this);

        if(!folder_this.exists()) folder_this.mkdirs();

        File frun=new File(folder_this, file_run_txt);
        if(!frun.exists()) {
            try {
                Utils.writeFile(frun.toString(), "file_main=main.py");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File fmain=new File(folder_this,file_name);
        if(!fmain.exists()) {
            try {
                fmain.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeNBT();
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
        initScript(null);
    }

    public void initScript(InitCallBack call_init){
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(new File(folder_this, file_run_txt));
            props.load(in);
            in.close();

            file_run=props.getProperty("file_main","main.py");
        } catch (IOException e) {
            e.printStackTrace();
        }

        th_run=new Thread(){
            @Override
            public void run() {
                init_ok=false;
                PythonInterpreter interpreter = PythonScript.createInterpreter();
                try {
                    interpreter.exec("chip_path='"+folder_this.toString().replace("\\","/")+"'");
                    if(call_init!=null)
                        call_init.init(interpreter);
                    interpreter.execfile(new File(folder_this,file_run).toString());
                }catch (Throwable e){
                    e.printStackTrace();
                }
                script=new PythonScript(interpreter);
                init_ok=true;
            }
        };
        th_run.start();
    }

    public List<String> getAllScripts(){
        List<String> files = new ArrayList<String>(Arrays.asList(folder_this.list()));
        return files;
    }

    public String readFromFile(String name){
        String res="";
        try {
            System.out.print(new File(folder_this,name).getAbsolutePath());
            res= Utils.readFile(new File(folder_this,name).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void saveToFile(String name, String code){
        System.out.println("save:"+stack+";"+folder_this);
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

    public void createFile(String name){
        File file=new File(folder_this,name);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Thread th_run;

    public void runFuncThread(String name,Object... paras){
        th_run= new Thread(() -> {
            System.out.println("wait init");
            while (!init_ok){}
            System.out.println("init ok");
            script.callFunction(name, paras);
            System.out.println("exec ok");
        });
        th_run.start();
    }

    public void stopRunning(){
        try {
            th_run.stop();
        }catch (Throwable e){
            e.printStackTrace();
        }

    }

    public interface InitCallBack{
        void init(PythonInterpreter interpreter);
    }
}
